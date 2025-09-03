package uno.moni.onex.business.user.pojo.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

data class BuildUser(
    @NotNull(message = "用户名称不能为空")
    @Schema(description = "用户名称")
    var name: String? = null,
    @NotNull(message = "用户名不能为空")
    @Schema(description = "注册用户名")
    var userName: String? = null,
    @NotNull(message = "密码不能为空")
    @Schema(description = "登录密码")
    var password: String? = null,
    @Schema(description = "用户权限类型：0超级管理员、1管理员、2普通用户（仅超管可用）")
    var userType: Int? = null,
)