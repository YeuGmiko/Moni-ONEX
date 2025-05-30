package uno.moni.onex.business.question.service.impl

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
import uno.moni.onex.business.question.service.QuestionService
import uno.moni.onex.business.question.service.QuestionSubmitOptionService
import uno.moni.onex.common.base.BaseServiceImpl
import uno.moni.onex.common.core.util.SecureUtils
import uno.moni.onex.open.vo.OpenQuestionVo
import uno.moni.onex.open.vo.OpenQuestionOrderVo
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

    override fun build(create: CreateQuestion): Question {
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

    private fun toOpenBaseVo(domain: Question): OpenQuestionVo {
        val vo = OpenQuestionVo()
        vo.id = domain.id
        vo.title = domain.title
        vo.content = domain.content
        vo.displayOrder = domain.displayOrder
        vo.createTime = domain.createTime
        vo.moduleId = domain.moduleId
        /* no properties: optionsLen moduleName accomplishStatus */
        /* empty properties: userSubmits */
        return vo
    }

    override fun toVos(domains: Collection<Question>): List<QuestionVo> {
        val moduleIds = domains.map{ it.moduleId }.toHashSet()
        if (domains.isEmpty() || moduleIds.isEmpty()) return listOf()
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
            vo
        }
        val ids = domains.map { it.id }
        val optionsWrapper = KtQueryWrapper(QuestionOption::class.java).`in`(QuestionOption::questionId, ids)
        val options = questionOptionService.list(optionsWrapper)
        vos.forEach { vo ->
            vo.optionsLen = options.filter { option -> option.questionId == vo.id }.size
        }
        return vos
    }

    override fun toSubmitOptionVos(domains: Collection<QuestionOption>): List<QuestionOptionVo> {
        return domains.map { domain ->
            val vo = QuestionOptionVo()
            vo.id = domain.id
            vo.orderNo = domain.orderNo
            vo.answer = domain.answer
            /* return */
            vo
        }
    }

    override fun toOpenVos(domains: Collection<Question>, userId: String?): List<OpenQuestionVo> {
        /* 准备好所有符合条件的数据，减少MYSQL连接次数 */
        val moduleIds = domains.map { it.moduleId }.toHashSet()
        /* 不符合查询条件，直接返回空 */
        if (domains.isEmpty() || moduleIds.isEmpty()) return listOf()
        val modules =
            moduleMapper.selectList(KtQueryWrapper(ModuleDomain::class.java).`in`(ModuleDomain::id, moduleIds))
        /* 使用Map便于快速查询模块信息 */
        val moduleMap = mutableMapOf<String, ModuleDomain>()
        modules.forEach { module ->
            val moduleId = module.id
            if (moduleId != null) moduleMap[moduleId] = module
        }
        /* 基础数据处理 */
        val vos = domains.map { domain ->
            val vo = toOpenBaseVo(domain)
            val module = moduleMap[domain.moduleId]
            vo.moduleId = domain.id
            vo.moduleName = module?.name ?: ""
            vo.moduleLabel = module?.label ?: ""
            vo.themeColor = module?.bgColor ?: ""
            /* no properties: optionsLen accomplishStatus */
            /* empty properties: userSubmits */
            vo
        }
        val ids = domains.map { it.id }
        /* 查询题目集合中的所有选项 */
        val questionOptionsWrapper = KtQueryWrapper(QuestionOption::class.java).`in`(QuestionOption::questionId, ids)
        val questionOptions = questionOptionService.list(questionOptionsWrapper)
        vos.forEach { vo ->
            /* 用户的题目完成状态：未登录则为0 */
            var accomplishStatus = 0
            if (userId != null) {
                /* 查询用户所提交的选项 */
                val submitOptionsWrapper =
                    KtQueryWrapper(QuestionSubmitOption::class.java).eq(QuestionSubmitOption::userId, userId).eq(
                        QuestionSubmitOption::questionId, vo.id
                    )
                val submitOptions = questionSubmitOptionMapper.selectList(submitOptionsWrapper)
                /* 判断是否有错误答案（value=1） */
                for (option in submitOptions) {
                    if (option.isRight != true) {
                        accomplishStatus = 1
                        break
                    }
                }
                /* 判断是否为全对（value=2） */
                if (accomplishStatus == 0 && submitOptions.isNotEmpty()) accomplishStatus = 2
                /* 用户提交的选项 */
                vo.userSubmits = questionSubmitOptionService.toVos(submitOptions)
            }
            /* 题目完成状态默认值：未提交（value=0） */
            val currentOptions = questionOptions.filter { option -> option.questionId == vo.id }

            vo.accomplishStatus = accomplishStatus
            vo.optionsLen = currentOptions.size
            /* return */
            vo
        }
        /* 返回数据 */
        return vos
    }

    override fun loadOpenedByModuleId(moduleId: String, userId: String?): List<OpenQuestionVo> {
        val moduleWrapper = KtQueryWrapper(ModuleDomain::class.java).eq(ModuleDomain::id, moduleId).eq(ModuleDomain::isOpen, true)
        if (!moduleMapper.exists(moduleWrapper)) throw RuntimeException("该模块[id=${moduleId}]不存在或未开放")
        val questions = questionMapper.selectList(KtQueryWrapper(Question::class.java).eq(Question::moduleId, moduleId))
        return toOpenVos(questions, userId)
    }

    override fun loadOpenById(id: String, userId: String): OpenQuestionVo {
        val wrapper = KtQueryWrapper(Question::class.java).eq(Question::id, id)
        val question: Question? = getOne(wrapper)
        if (question == null) throw RuntimeException("题目[id=$id]不存在")
        return toOpenVos(listOf(question), userId).first()
    }

    override fun loadOrdersByModuleId(
        moduleId: String,
        userId: String?
    ): List<OpenQuestionOrderVo> {
        val moduleWrapper = KtQueryWrapper(ModuleDomain::class.java).eq(ModuleDomain::id, moduleId).eq(ModuleDomain::isOpen, true)
        if (!moduleMapper.exists(moduleWrapper)) throw RuntimeException("该模块[id=${moduleId}]不存在或未开放")
        val questions = questionMapper.selectList(KtQueryWrapper(Question::class.java).eq(Question::moduleId, moduleId))
        return toOrders(toOpenVos(questions, userId))
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
            domain.isRight = submit.answer.equals(option.answer)
            domain.userAnswer = submit.answer
            domain.optionId = option.id
            domain.questionId = questionId
            domain.userId = userId
            /* return */
            domain
        }
        questionSubmitOptionMapper.insert(submits)
        return toSubmitOptionVos(options)
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

    private fun toOrders(questions: List<OpenQuestionVo>): List<OpenQuestionOrderVo> {
        return questions.map { question ->
            val vo = OpenQuestionOrderVo()
            vo.id = question.id
            vo.title = question.title
            vo.accomplishStatus = question.accomplishStatus
            vo.displayOrder = question.displayOrder
            /* return */
            vo
        }
    }
}