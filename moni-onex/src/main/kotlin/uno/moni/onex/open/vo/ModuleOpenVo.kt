package uno.moni.onex.open.vo

data class ModuleOpenVo(
    var id: String? = null,
    var label: String? = null,
    var name: String? = null,
    var bgColor: String? = null,
    var displayOrder: Int? = null,
    var isOpen: Boolean? = null,
    var questionCount: Int? = null,
    var remark: String? = null,

    var questions: List<QuestionOrderOpenVo> = emptyList(),
) {
}