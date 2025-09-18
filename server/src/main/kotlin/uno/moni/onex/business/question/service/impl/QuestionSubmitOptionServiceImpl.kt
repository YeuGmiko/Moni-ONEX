package uno.moni.onex.business.question.service.impl

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import org.springframework.stereotype.Service
import uno.moni.onex.business.question.mapper.QuestionOptionMapper
import uno.moni.onex.business.question.mapper.QuestionSubmitOptionMapper
import uno.moni.onex.business.question.pojo.domain.QuestionOption
import uno.moni.onex.business.question.pojo.domain.QuestionSubmitOption
import uno.moni.onex.business.question.pojo.vo.QuestionSubmitOptionVo
import uno.moni.onex.business.question.service.QuestionSubmitOptionService
import uno.moni.onex.core.base.BaseServiceImpl

@Service
class QuestionSubmitOptionServiceImpl(private val questionOptionMapper: QuestionOptionMapper) : QuestionSubmitOptionService, BaseServiceImpl<QuestionSubmitOptionMapper, QuestionSubmitOption>() {
    override fun toBaseVos(domains: List<QuestionSubmitOption>): List<QuestionSubmitOptionVo> {
        return domains.map { domain ->
            val vo = QuestionSubmitOptionVo()
            vo.id = domain.id
            vo.submitDate = domain.submitDate
            vo.userAnswer = domain.userAnswer
            vo.optionId = domain.optionId
            vo.questionId = domain.questionId
            vo.userId = domain.userId
            /* no properties: orderNo */
            vo
        }
    }

    override fun toVos(domains: List<QuestionSubmitOption>): List<QuestionSubmitOptionVo> {
        val optionIds = domains.map{ it.optionId }
        /* 慎防SQL IN查询列表为空！！！*/
        if (optionIds.isEmpty()) return emptyList()
        val answerWrapper = KtQueryWrapper(QuestionOption::class.java).`in`(QuestionOption::id, optionIds)
        val answers = questionOptionMapper.selectList(answerWrapper)

        return domains.map { domain ->
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
            vo
        }
    }
}