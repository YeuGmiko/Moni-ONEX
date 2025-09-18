package uno.moni.onex.business.question.pojo.vo

import java.io.Serializable
import java.time.LocalDateTime

data class QuestionVo(
    var id: String? = null,
    var title: String? = null,
    var content: String? = null,
    var displayOrder: Int? = null,
    var createTime: LocalDateTime? = null,
    var optionsLen: Int? = null,
    var moduleId: String? = null,
    var moduleName: String? = null,
): Serializable