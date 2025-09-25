package uno.moni.onex.admin.pojo.vo

import io.swagger.v3.oas.annotations.media.Schema

data class UserVo(
    @Schema(description = "用户ID")
    var userId: String? = null,
    @Schema(description = "用户昵称")
    var name: String? = null,
    @Schema(description = "注册用户名")
    var userName: String? = null,
    @Schema(description = "用户角色列表")
    var roles: MutableList<String> = mutableListOf(),
    @Schema(description = "用户状态：（0不可用，1正常，2封禁）")
    var status: Int? = null,
)