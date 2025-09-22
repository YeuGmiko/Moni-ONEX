package uno.moni.onex.business.question.pojo.dto

data class BuildQuestion(
    var title: String? = null,
    var content: String? = null,
    var order: Int? = null,
    var options: MutableList<BuildQuestionOption> = mutableListOf(),
)