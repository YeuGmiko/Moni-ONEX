package uno.moni.onex.open.vo

import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable

data class OpenQuestionOrderVo(
    var id: String? = null,
    var title: String? = null,
    @Schema(description = "完成状态：0未提交、1部分/全部错误、1全部正确")
    var accomplishStatus: Int? = null,
    var displayOrder: Int? = null,
): Serializable