package uno.moni.onex.business.auth.pojo.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

class UserLoginBody(
    @NotNull(message = "用户名不能为空")
    @Schema(description = "用户身份")
    var userName: String? = null,

    @NotNull(message = "用户凭证不能为空")
    @Schema(description = "用户登录凭证")
    var password: String? = null,

    @Schema(description = "是否保持登录状态（默认值：false）", defaultValue = "false")
    var rememberMe: Boolean = false,
)