package uno.moni.onex.business.question.pojo.dto

import java.io.Serializable

data class UpdateQuestion(
    var title: String? = null,
    var content: String? = null,
    var order: Int? = null,
): Serializable