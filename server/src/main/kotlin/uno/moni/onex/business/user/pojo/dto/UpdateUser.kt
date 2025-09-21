package uno.moni.onex.business.user.pojo.dto

import io.swagger.v3.oas.annotations.media.Schema

class UpdateUser(
    @Schema(description = "用户名称")
    var name: String? = "",
)