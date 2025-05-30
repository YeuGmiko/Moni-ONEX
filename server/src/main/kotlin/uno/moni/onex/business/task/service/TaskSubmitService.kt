package uno.moni.onex.business.task.service

import uno.moni.onex.business.question.pojo.vo.QuestionSubmitOptionVo
import uno.moni.onex.business.task.pojo.domain.TaskSubmit
import uno.moni.onex.common.base.BaseService

interface TaskSubmitService: BaseService<TaskSubmit> {
    fun toBaseVo(domain: TaskSubmit): QuestionSubmitOptionVo
}