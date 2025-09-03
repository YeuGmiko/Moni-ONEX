package uno.moni.onex.business.auth.pojo.vo

import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable
import java.time.LocalDateTime

data class LoginUser(
    @Schema(description = "用户ID")
    var userId: String? = null,
    @Schema(description = "登录时间")
    var loginTime: LocalDateTime? = null,
    @Schema(description = "会话凭证Token")
    var token: String? = null,
    @Schema(description = "是否保持登录状态")
    var isRememberMe: Boolean? = null
): Serializable