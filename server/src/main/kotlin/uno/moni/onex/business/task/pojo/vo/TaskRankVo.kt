package uno.moni.onex.business.task.pojo.vo

import java.io.Serializable

data class TaskRankVo(
    var rank: Int? = null,
    var name: String? = null,
    var userName: String? = null,
    var rightOptions: Int? = null,
    var submitOptions: Int? = null,
    var totalOptions: Int? = null,
    var rightQuestions: Int? = null,
    var submitQuestion: Int? = null,
    var totalQuestions: Int? = null,
): Serializable