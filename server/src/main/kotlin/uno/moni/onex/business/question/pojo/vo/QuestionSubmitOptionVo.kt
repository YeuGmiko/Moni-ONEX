package uno.moni.onex.business.question.pojo.vo

import java.io.Serializable
import java.time.LocalDate

data class QuestionSubmitOptionVo(
    var id: String? = null,
    var orderNo: Int? = null,
    var submitDate: LocalDate? = null,
    var userAnswer: String? = null,
    var isRight: Boolean? = null,
    var optionId: String? = null,
    var questionId: String? = null,
    var userId: String? = null,
): Serializable