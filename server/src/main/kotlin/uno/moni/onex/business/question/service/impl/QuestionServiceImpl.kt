package uno.moni.onex.business.question.service.impl

import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import org.springframework.stereotype.Service
import uno.moni.onex.business.question.mapper.ModuleMapper
import uno.moni.onex.business.question.mapper.QuestionMapper
import uno.moni.onex.business.question.mapper.QuestionOptionMapper
import uno.moni.onex.business.question.mapper.QuestionSubmitOptionMapper
import uno.moni.onex.business.question.pojo.domain.ModuleDomain
import uno.moni.onex.business.question.pojo.domain.Question
import uno.moni.onex.business.question.pojo.domain.QuestionOption
import uno.moni.onex.business.question.pojo.domain.QuestionSubmitOption
import uno.moni.onex.business.question.pojo.dto.BuildQuestion
import uno.moni.onex.business.question.pojo.dto.BuildQuestionOption
import uno.moni.onex.business.question.pojo.dto.CreateQuestion
import uno.moni.onex.business.question.pojo.dto.UpdateQuestion
import uno.moni.onex.business.question.pojo.vo.QuestionOptionVo
import uno.moni.onex.business.question.pojo.vo.QuestionSubmitOptionVo
import uno.moni.onex.business.question.pojo.vo.QuestionVo
import uno.moni.onex.business.question.service.QuestionOptionService
import uno.moni.onex.business.question.service.QuestionOrderService
import uno.moni.onex.business.question.service.QuestionService
import uno.moni.onex.business.question.service.QuestionSubmitOptionService
import uno.moni.onex.core.base.BaseServiceImpl
import uno.moni.onex.core.core.util.SecureUtils
import uno.moni.onex.open.vo.OpenQuestionVo
import uno.moni.onex.open.vo.QuestionOrderOpenVo
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.collections.get
import kotlin.collections.map

@Service
class QuestionServiceImpl(
    private val moduleMapper: ModuleMapper,
    private val questionOptionMapper: QuestionOptionMapper,
    private val questionSubmitOptionMapper: QuestionSubmitOptionMapper,
    private val questionMapper: QuestionMapper,
    private val questionSubmitOptionService: QuestionSubmitOptionService,
    private val questionOptionService: QuestionOptionService,
    private val questionOrderService: QuestionOrderService,
) : QuestionService, BaseServiceImpl<QuestionMapper, Question>() {

    override fun loadByModuleId(moduleId: String): List<QuestionVo> {
        val wrapper = KtQueryWrapper(Question::class.java).eq(Question::moduleId, moduleId)
        return toVos(list(wrapper))
    }

    override fun load(id: String): Question? {
        val wrapper = KtQueryWrapper(Question::class.java).eq(Question::id, id)
        return try {
            getOne(wrapper)
        } catch (e: Exception) {
            null
        }
    }

    private fun build(create: CreateQuestion): Question {
        val domain = Question()
        domain.id = SecureUtils.generateId();
        domain.title = create.title
        domain.content = create.content
        domain.displayOrder = create.order
        domain.moduleId = create.moduleId
        domain.createTime = LocalDateTime.now()
        return domain
    }

    override fun create(buildBody: BuildQuestion, moduleId: String): String {
        val wrapper = KtQueryWrapper(Question::class.java).eq(Question::moduleId, moduleId).eq(Question::title, buildBody.title)
        if (exists(wrapper)) throw RuntimeException("该题目[${buildBody.title}]已存在")
        val create = CreateQuestion()
        create.title = buildBody.title
        create.content = buildBody.content
        create.order = buildBody.order
        create.moduleId = moduleId
        /* build question */
        val domain = build(create)
        if (!save(domain))  throw RuntimeException("创建题目失败，服务器错误")
        val domainId = domain.id
        if (domainId == null) throw RuntimeException("服务器业务逻辑错误")
        return domainId
    }

    override fun update(updateBody: UpdateQuestion, id: String) {
        val wrapper = KtQueryWrapper(Question::class.java).eq(Question::id, id)
        val question = getOne(wrapper)
        question.displayOrder = updateBody.order
        question.title = updateBody.title
        question.content = updateBody.content
        if (!updateById(question)) throw RuntimeException("更新信息失败")
    }

    override fun delete(id: String) {
        val wrapper = KtQueryWrapper(Question::class.java).eq(Question::id, id)
        if (!exists(wrapper)) throw RuntimeException("该题目[id=${id}]不存在")
        remove(wrapper)
    }

    override fun toVo(domain: Question): QuestionVo {
        val optionWrapper = KtQueryWrapper(QuestionOption::class.java).eq(QuestionOption::questionId, domain.id)
        val moduleWrapper = KtQueryWrapper(ModuleDomain::class.java).eq(ModuleDomain::id, domain.moduleId)
        val options = questionOptionService.list(optionWrapper)
        val module = moduleMapper.selectOne(moduleWrapper)
        val vo = toBaseVo(domain)
        vo.optionsLen = options.size
        vo.moduleName = module?.name ?: ""
        return vo
    }

    private fun toBaseVo(domain: Question): QuestionVo {
        val vo = QuestionVo()
        vo.id = domain.id
        vo.title = domain.title
        vo.content = domain.content
        vo.displayOrder = domain.displayOrder
        vo.createTime = domain.createTime
        vo.moduleId = domain.moduleId
        /* no properties: optionsLen moduleName */
        return vo
    }

    override fun toOpenVo(userId: String?, domain: Question): OpenQuestionVo {
        val answers = questionOptionMapper.selectList(KtQueryWrapper(QuestionOption::class.java).eq(QuestionOption::questionId, domain.id))
        val submits = questionSubmitOptionMapper.selectList(KtQueryWrapper(QuestionSubmitOption::class.java)
            .eq(QuestionSubmitOption::questionId, domain.id).eq(QuestionSubmitOption::userId, userId))
        val vo = OpenQuestionVo()
        vo.id = domain.id
        vo.title = domain.title
        vo.content = domain.content
        vo.displayOrder = domain.displayOrder
        vo.createTime = domain.createTime
        vo.moduleId = domain.moduleId
        vo.optionsLen = answers.size
        vo.accomplishStatus = questionOrderService.calculateAccomplishStatus(submits, answers)
        vo.userSubmits = submits.map { domain ->
            val answer = answers.find{ it.id == domain.optionId }
            if (answer == null) throw RuntimeException("服务器逻辑错误")
            val vo = QuestionSubmitOptionVo()
            vo.id = domain.id
            vo.submitDate = domain.submitDate
            vo.orderNo = answer.orderNo
            vo.userAnswer = domain.userAnswer
            vo.isRight = answer.answer.equals(domain.userAnswer)
            vo.optionId = domain.optionId
            vo.questionId = domain.questionId
            vo.userId = domain.userId
            return@map vo
        }
        return vo
    }

    override fun toVos(domains: Collection<Question>): List<QuestionVo> {
        val moduleIds = domains.map{ it.moduleId }.toHashSet()
        /* 慎防SQL IN查询列表为空！！！*/
        if (moduleIds.isEmpty()) return emptyList()
        val modules = moduleMapper.selectList(KtQueryWrapper(ModuleDomain::class.java).`in`(ModuleDomain::id, moduleIds))
        val map = mutableMapOf<String, ModuleDomain>()
        modules.forEach { module ->
            val moduleId = module.id
            if (moduleId != null) map[moduleId] = module
        }
        val vos = domains.map { domain ->
            val vo = toBaseVo(domain)
            vo.moduleName = map[domain.moduleId]?.name ?: ""
            /* no properties: optionsLen */
            return@map vo
        }
        val ids = domains.map { it.id }
        /* 慎防SQL IN查询列表为空！！！*/
        if (ids.isEmpty()) return emptyList()
        val optionsWrapper = KtQueryWrapper(QuestionOption::class.java).`in`(QuestionOption::questionId, ids)
        val options = questionOptionService.list(optionsWrapper)
        vos.forEach { vo ->
            vo.optionsLen = options.filter { option -> option.questionId == vo.id }.size
        }
        return vos
    }

    override fun toQuestionOptionVos(domains: List<QuestionOption>): List<QuestionOptionVo> {
        return domains.map { domain ->
            val vo = QuestionOptionVo()
            vo.id = domain.id
            vo.orderNo = domain.orderNo
            vo.answer = domain.answer
            return@map vo
        }
    }

    override fun loadOpenById(id: String, userId: String): OpenQuestionVo {
        val wrapper = KtQueryWrapper(Question::class.java).eq(Question::id, id)
        val question: Question? = getOne(wrapper)
        if (question == null) throw RuntimeException("题目[id=$id]不存在")
        return toOpenVo(userId, question)
    }

    override fun loadOrdersByModuleId(
        page: Long,
        size: Long,
        moduleId: String,
        userId: String?
    ): IPage<QuestionOrderOpenVo> {
        return questionOrderService.getVoPageByModuleId(page, size, moduleId, userId)
    }

    override fun postQuestionSubmits(
        questionId: String,
        answers: List<BuildQuestionOption>,
        userId: String
    ): List<QuestionOptionVo> {
        val question = questionMapper.selectOne(KtQueryWrapper(Question::class.java).eq(Question::id, questionId))
        if (question == null) throw RuntimeException("题目[id=${questionId}]不存在")
        val moduleExisted = moduleMapper.exists(KtQueryWrapper(ModuleDomain::class.java).eq(ModuleDomain::id, question.moduleId).eq(
            ModuleDomain::isOpen, true
        ))
        if (!moduleExisted) throw RuntimeException("题目所属模块[id=${questionId}]不存在或不公开")
        val options = questionOptionMapper.selectList(KtQueryWrapper(QuestionOption::class.java).eq(QuestionOption::questionId, questionId))
        val submits = options.map { option ->
            val submit = answers.find { it.orderNo == option.orderNo }
            if (submit == null) throw RuntimeException("找不到选项[id=${questionId}]")
            val domain = QuestionSubmitOption()
            domain.id = SecureUtils.generateId()
            domain.submitDate = LocalDate.now()
            domain.userAnswer = submit.answer
            domain.optionId = option.id
            domain.questionId = questionId
            domain.userId = userId
            return@map domain
        }
        questionSubmitOptionMapper.insert(submits)
        return toQuestionOptionVos(options)
    }

    override fun loadQuestionSubmits(
        questionId: String,
        userId: String
    ): List<QuestionSubmitOptionVo> {
        val questionWrapper = KtQueryWrapper(Question::class.java).eq(Question::id, questionId)
        val question = questionMapper.selectOne(questionWrapper)
        if (question == null) throw RuntimeException("题目[id=${questionId}]不存在")
        val moduleWrapper = KtQueryWrapper(ModuleDomain::class.java).eq(ModuleDomain::id, question.moduleId).eq(
            ModuleDomain::isOpen, true)
        if (!moduleMapper.exists(moduleWrapper)) throw RuntimeException("模块[id=${questionId}]不存在或未开放")
        val submitWrapper = KtQueryWrapper(QuestionSubmitOption::class.java).eq(QuestionSubmitOption::questionId, questionId).eq(
            QuestionSubmitOption::userId, userId)
        val domains = questionSubmitOptionService.list(submitWrapper)
        return questionSubmitOptionService.toVos(domains)
    }

    override fun loadQuestionAnswers(
        questionId: String,
        userId: String
    ): List<QuestionOptionVo> {
        val submitWrapper = KtQueryWrapper(QuestionSubmitOption::class.java).eq(QuestionSubmitOption::questionId, questionId
        ).eq(QuestionSubmitOption::userId, userId)
        if (!questionSubmitOptionService.exists(submitWrapper)) throw RuntimeException("该用户未提交，不允许查询答案")
        return questionOptionService.loadByQuestionId(questionId)
    }
}