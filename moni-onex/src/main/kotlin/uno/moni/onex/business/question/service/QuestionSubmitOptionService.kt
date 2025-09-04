package uno.moni.onex.business.question.service

import uno.moni.onex.business.question.pojo.domain.QuestionSubmitOption
import uno.moni.onex.business.question.pojo.vo.QuestionSubmitOptionVo
import uno.moni.onex.core.base.BaseService

interface QuestionSubmitOptionService: BaseService<QuestionSubmitOption> {
    fun toBaseVos(domains: List<QuestionSubmitOption>): List<QuestionSubmitOptionVo>
    fun toVos(domains: List<QuestionSubmitOption>): List<QuestionSubmitOptionVo>
}