package uno.moni.onex.admin.controller

import cn.dev33.satoken.annotation.SaCheckDisable
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.annotation.SaMode
import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uno.moni.onex.admin.pojo.dto.CreateUser
import uno.moni.onex.admin.pojo.dto.UpdateUser
import uno.moni.onex.admin.pojo.vo.UserVo
import uno.moni.onex.business.user.enums.UserTypeEnums
import uno.moni.onex.business.user.service.UserService
import uno.moni.onex.business.user.pojo.domain.User
import uno.moni.onex.core.enums.ResponseCodeEnums
import uno.moni.onex.core.pojo.vo.Response

@SaCheckDisable
@SaCheckRole(value = ["SUPER_ADMIN", "ADMIN_USER"], mode = SaMode.OR)
@RestController("AdminUserController")
@RequestMapping("/admin/users")
@Tag(name = "用户管理")
class UserController(
    var userService: UserService,
) {
    @Transactional
    @Operation(summary = "批量导入普通用户")
    @PostMapping("/common/upload")
    fun createUsers(@RequestBody users: MutableList<CreateUser>): Response<Unit> {
        users.forEach { user ->
            userService.create(user, UserTypeEnums.COMMON_USER)
        }
        return Response.success()
    }

    @Operation(summary = "创建普通用户")
    @PostMapping("/common")
    fun createUser(@RequestBody body: CreateUser): Response<Unit> {
        userService.create(body, UserTypeEnums.COMMON_USER)
        return Response.success()
    }

    @Operation(summary = "批量创建普通用户")
    @PostMapping("/common/batch")
    fun createUserBatch(@RequestBody body: List<CreateUser>): Response<Unit> {
        body.forEach { create ->
            userService.create(create, UserTypeEnums.COMMON_USER)
        }
        return Response.success()
    }

    @Operation(summary = "删除普通用户")
    @DeleteMapping("/common/{id}")
    fun deleteUser(@PathVariable("id") id: String): Response<Unit> {
        userService.deleteByRole(id, UserTypeEnums.COMMON_USER)
        return Response.success()
    }

    @Operation(summary = "创建管理员")
    @SaCheckRole("SUPER_ADMIN")
    @PostMapping("/admin")
    fun createAdmin(@RequestBody body: CreateUser): Response<Unit> {
        userService.create(body, UserTypeEnums.ADMIN_USER)
        return Response.success()
    }

    @Operation(summary = "删除管理员")
    @SaCheckRole("SUPER_ADMIN")
    @DeleteMapping("/admin/{id}")
    fun deleteAdmin(@PathVariable("id") id: String): Response<Unit> {
        userService.deleteByRole(id, UserTypeEnums.ADMIN_USER)
        return Response.success()
    }

    @Operation(summary = "获取所有普通用户")
    @GetMapping("/common")
    fun fetchCommonUsers(): Response<List<UserVo>> {
        val wrapper = KtQueryWrapper(User::class.java).eq(User::userType, UserTypeEnums.COMMON_USER.type)
        val vos = userService.list(wrapper).map { user ->
            userService.toVo(user)
        }
        return Response.success().data(vos)
    }

    @Operation(summary = "获取所有管理员用户")
    @SaCheckRole("SUPER_ADMIN")
    @GetMapping("/admin")
    fun fetchAdminUsers(): Response<List<UserVo>> {
        val wrapper = KtQueryWrapper(User::class.java).eq(User::userType, UserTypeEnums.ADMIN_USER.type)
        val vos = userService.list(wrapper).map { user ->
            userService.toVo(user)
        }
        return Response.success().data(vos)
    }

    @Operation(
        summary = "强制更改用户信息"
    )
    @PutMapping("/{id}")
    fun updateUserForce(
        @PathVariable("id") userId: String,
        @RequestBody updateUser: UpdateUser,
    ): Response<Unit> {
        val operatorId = StpUtil.getLoginId().toString()
        userService.updateUser(operatorId, userId, updateUser)
        return Response.success(ResponseCodeEnums.SUCCESS_NO_CONTENT.code, "用户信息更新成功")
    }

    @Operation(
        summary = "更改用户封禁",
        parameters = [
            Parameter(name = "type", description = "1为封禁，0为解禁", required = false)
        ]
    )
    @GetMapping("/ban/{id}")
    fun changeUserBan(
        @PathVariable("id") userId: String,
        @RequestParam("type", defaultValue = "1") type: Int
    ): Response<Unit> {
        val operatorId = StpUtil.getLoginId().toString()
        userService.changeBanned(userId, type == 1)
        return Response.success(ResponseCodeEnums.SUCCESS_NO_CONTENT.code, "操作成功")
    }
}