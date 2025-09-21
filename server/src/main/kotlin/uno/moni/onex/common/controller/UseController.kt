package uno.moni.onex.common.controller

import cn.dev33.satoken.stp.StpUtil
import cn.dev33.satoken.annotation.SaCheckLogin
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uno.moni.onex.business.user.pojo.dto.UpdateUser
import uno.moni.onex.business.user.pojo.dto.UpdateUserAuthPassword
import uno.moni.onex.business.user.service.UserService
import uno.moni.onex.common.pojo.vo.UserCommonVo
import uno.moni.onex.core.enums.ResponseCodeEnums
import uno.moni.onex.core.pojo.vo.Response

@RestController("CommonUserController")
@RequestMapping("/users")
@Tag(name = "用户公共模块")
class UseController(
    private val userService: UserService
) {
    @Operation(summary = "获取当前用户信息（请不要使用，暂时用/auth/info替代）")
    @GetMapping("/info")
    fun getUserInfo(): Response<UserCommonVo> {
        val userId = StpUtil.getLoginId().toString()
        return Response.Companion.success().data(userService.loadCommon(userId))
    }

    @SaCheckLogin
    @Operation(summary = "更改当前用户信息")
    @PutMapping("/info")
    fun updateUserInfo(
        @RequestBody update: UpdateUser
    ): Response<Unit> {
        val userId = StpUtil.getLoginId().toString()
        userService.updateCommon(userId, update)
        return Response.success(ResponseCodeEnums.SUCCESS_NO_CONTENT.code, "用户信息更改成功")
    }

    @SaCheckLogin
    @Operation(summary = "更改当前用户密码")
    @PutMapping("/auth/password")
    fun updateUserPassword(
        @RequestBody update: UpdateUserAuthPassword
    ): Response<Unit> {
        val userId = StpUtil.getLoginId().toString()
        userService.updatePassword(userId, update)
        return Response.success(ResponseCodeEnums.SUCCESS_NO_CONTENT.code, "用户密码更改成功")
    }
}