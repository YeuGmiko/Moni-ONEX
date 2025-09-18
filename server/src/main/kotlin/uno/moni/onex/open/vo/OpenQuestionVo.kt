package uno.moni.onex.open.vo

import io.swagger.v3.oas.annotations.media.Schema
import uno.moni.onex.business.question.pojo.vo.QuestionSubmitOptionVo
import java.io.Serializable
import java.time.LocalDateTime

data class OpenQuestionVo(
    var id: String? = null,
    var title: String? = null,
    var content: String? = null,
    var displayOrder: Int? = null,
    var createTime: LocalDateTime? = null,
    var optionsLen: Int? = null,
    var moduleId: String? = null,
    @Schema(description = "完成状态：0未提交、1部分/全部错误、2全部正确")
    var accomplishStatus: Int? = null,
    var userSubmits: List<QuestionSubmitOptionVo> = mutableListOf(),
): Serializable