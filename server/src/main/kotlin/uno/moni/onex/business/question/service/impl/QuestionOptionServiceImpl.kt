package uno.moni.onex.business.question.service.impl

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import org.springframework.stereotype.Service
import uno.moni.onex.business.question.mapper.QuestionOptionMapper
import uno.moni.onex.business.question.pojo.domain.QuestionOption
import uno.moni.onex.business.question.pojo.dto.BuildQuestionOption
import uno.moni.onex.business.question.pojo.dto.CreateQuestionOption
import uno.moni.onex.business.question.pojo.vo.QuestionOptionVo
import uno.moni.onex.business.question.service.QuestionOptionService
import uno.moni.onex.core.base.BaseServiceImpl
import uno.moni.onex.core.core.util.SecureUtils

@Service
class QuestionOptionServiceImpl: QuestionOptionService, BaseServiceImpl<QuestionOptionMapper, QuestionOption>() {
    override fun loadByQuestionId(questionId: String): List<QuestionOptionVo> {
        val wrapper = KtQueryWrapper(QuestionOption::class.java).eq(QuestionOption::questionId, questionId)
        return list(wrapper).map{ domain  ->
            toVo(domain)
        }.toList()
    }

    override fun load(id: String): QuestionOption? {
        val wrapper = KtQueryWrapper(QuestionOption::class.java).eq(QuestionOption::id, id)
        return try {
            getOne(wrapper)
        } catch (e: Exception) {
            null
        }
    }

    private fun build(create: CreateQuestionOption): QuestionOption {
        val domain = QuestionOption()
        domain.id = SecureUtils.generateId();
        domain.questionId = create.questionId
        domain.orderNo = create.orderNo
        domain.answer = create.answer
        return domain
    }

    override fun create(buildBody: BuildQuestionOption, questionId: String) {
        val wrapper = KtQueryWrapper(QuestionOption::class.java).eq(QuestionOption::questionId, questionId).eq(
            QuestionOption::orderNo, buildBody.orderNo)
        if (exists(wrapper)) throw RuntimeException("该选项[order=${buildBody.orderNo}]已经存在")
        val create = CreateQuestionOption()
        create.questionId = questionId
        create.orderNo = buildBody.orderNo
        create.answer = buildBody.answer
        val domain = build(create)
        if (!save(domain)) throw RuntimeException("创建题目选项失败，服务器错误")
    }


    override fun createList(
        builds: List<BuildQuestionOption>,
        questionId: String
    ) {
        builds.forEach { build ->
            create(build, questionId)
        }
    }

    override fun toVo(domain: QuestionOption): QuestionOptionVo {
        val vo = QuestionOptionVo()
        vo.id = domain.id
        vo.orderNo = domain.orderNo
        vo.answer = domain.answer
        return vo
    }

    override fun toVos(domains: List<QuestionOption>): List<QuestionOptionVo> {
        return domains.map { domain ->
            val vo = QuestionOptionVo()
            vo.id = domain.id
            vo.orderNo = domain.orderNo
            vo.answer = domain.answer
            return@map vo
        }
    }

}