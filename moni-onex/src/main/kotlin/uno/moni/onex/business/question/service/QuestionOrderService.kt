package uno.moni.onex.business.question.service

import uno.moni.onex.business.question.pojo.domain.QuestionOption
import uno.moni.onex.business.question.pojo.domain.QuestionSubmitOption
import uno.moni.onex.open.vo.QuestionOrderOpenVo

interface QuestionOrderService {
    fun calculateAccomplishStatus(submits: List<QuestionSubmitOption>, answers: List<QuestionOption>): Int
    fun getVoListByModuleId(userId: String?,  moduleId: String): List<QuestionOrderOpenVo>
}