package uno.moni.onex.common.controller

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uno.moni.onex.admin.pojo.vo.UserVo
import uno.moni.onex.business.auth.pojo.dto.UserLoginBody
import uno.moni.onex.business.auth.pojo.vo.LoginUser
import uno.moni.onex.business.auth.service.LoginService
import uno.moni.onex.business.user.enums.UserTypeEnums
import uno.moni.onex.business.user.service.UserService
import uno.moni.onex.core.enums.ResponseCodeEnums
import uno.moni.onex.core.pojo.vo.Response

@RestController
@RequestMapping("/auth")
@Tag(name = "用户授权管理")
class AuthController(
    val loginService: LoginService,
    val userService: UserService,
) {
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    fun login(
        @RequestBody loginBody: UserLoginBody,
        @RequestParam(value = "admin", required = false) admin: Boolean?,
    ): Response<LoginUser> {
        val commonAllowRoles = listOf(UserTypeEnums.COMMON_USER.roleName)
        val adminAllowRoles = listOf(UserTypeEnums.ADMIN_USER.roleName, UserTypeEnums.SUPER_ADMIN.roleName)
        return if (admin == true) {
            Response.Companion.success().data(loginService.login(loginBody, adminAllowRoles, true))
        } else {
            Response.Companion.success().data(loginService.login(loginBody, commonAllowRoles, true))
        }
    }

    @SaCheckLogin
    @Operation(summary = "退出登录状态")
    @GetMapping("/logout")
    fun logout(): Response<Unit> {
        loginService.logout()
        return Response.Companion.success(ResponseCodeEnums.USER_LOGOUT.code, ResponseCodeEnums.USER_LOGOUT.msg)
    }

    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/info")
    @SaCheckLogin
    fun getCurrentInfo(): Response<UserVo> {
        val userId = StpUtil.getLoginId().toString().toLong()
        val user = userService.load(userId)
        if (user == null) {
            throw RuntimeException("该用户[${userId}]不存在")
        } else return Response.Companion.success().data(userService.toVo(user))
    }
}