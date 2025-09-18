package uno.moni.onex.business.question.pojo.vo

import java.io.Serializable

data class ModuleVo(
    var id: String? = null,
    var label: String? = null,
    var name: String? = null,
    var bgColor: String? = null,
    var displayOrder: Int? = null,
    var isOpen: Boolean? = null,
    var questionCount: Int? = null,
    var remark: String? = null,
): Serializable