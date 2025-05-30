package uno.moni.onex.business.task.service.impl

import org.springframework.stereotype.Service
import uno.moni.onex.business.question.pojo.vo.QuestionSubmitOptionVo
import uno.moni.onex.business.task.mapper.TaskSubmitMapper
import uno.moni.onex.business.task.pojo.domain.TaskSubmit
import uno.moni.onex.business.task.service.TaskSubmitService
import uno.moni.onex.common.base.BaseServiceImpl

@Service
class TaskSubmitServiceImpl: TaskSubmitService, BaseServiceImpl<TaskSubmitMapper, TaskSubmit>() {
    override fun toBaseVo(domain: TaskSubmit): QuestionSubmitOptionVo {
        val vo = QuestionSubmitOptionVo()
        vo.id = domain.id
        vo.submitDate = domain.submitDate
        vo.orderNo = domain.orderNo
        vo.userAnswer = domain.userAnswer
        vo.isRight = domain.isRight
        vo.optionId = domain.optionId
        vo.questionId = domain.questionId
        vo.userId = domain.userId
        /* no properties: orderNo */
        return vo
    }
}