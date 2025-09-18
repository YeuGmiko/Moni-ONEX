package uno.moni.onex.admin.controller

import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.annotation.SaMode
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uno.moni.onex.admin.pojo.dto.CreateUser
import uno.moni.onex.admin.pojo.vo.UserVo
import uno.moni.onex.business.user.enums.UserTypeEnums
import uno.moni.onex.business.user.service.UserService
import uno.moni.onex.business.user.pojo.domain.User
import uno.moni.onex.core.pojo.vo.Response

@RestController("AdminUserController")
@RequestMapping("/admin/users")
@Tag(name = "用户管理")
class UserController(
    var userService: UserService,
) {
    @Transactional
    @Operation(summary = "批量导入普通用户")
    @SaCheckRole(value = ["SUPER_ADMIN", "ADMIN_USER"], mode = SaMode.OR)
    @PostMapping("/common/upload")
    fun createUsers(@RequestBody users: MutableList<CreateUser>): Response<Unit> {
        users.forEach { user ->
            userService.create(user, UserTypeEnums.COMMON_USER.type)
        }
        return Response.success()
    }

    @Operation(summary = "创建普通用户")
    @SaCheckRole(value = ["SUPER_ADMIN", "ADMIN_USER"], mode = SaMode.OR)
    @PostMapping("/common")
    fun createUser(@RequestBody body: CreateUser): Response<Unit> {
        userService.create(body, UserTypeEnums.COMMON_USER.type)
        return Response.success()
    }
    @Operation(summary = "创建普通用户")
    @SaCheckRole(value = ["SUPER_ADMIN", "ADMIN_USER"], mode = SaMode.OR)
    @PostMapping("/common/batch")
    fun createUserBatch(@RequestBody body: List<CreateUser>): Response<Unit> {
        body.forEach { create ->
            userService.create(create, UserTypeEnums.COMMON_USER.type)
        }
        return Response.success()
    }

    @Operation(summary = "删除普通用户")
    @SaCheckRole(value = ["SUPER_ADMIN", "ADMIN_USER"], mode = SaMode.OR)
    @DeleteMapping("/common/{id}")
    fun deleteUser(@PathVariable("id") id: Long): Response<Unit> {
        userService.deleteByRole(id, UserTypeEnums.COMMON_USER.type)
        return Response.success()
    }

    @Operation(summary = "创建管理员")
    @SaCheckRole("SUPER_ADMIN")
    @PostMapping("/admin")
    fun createAdmin(@RequestBody body: CreateUser): Response<Unit> {
        userService.create(body, UserTypeEnums.ADMIN_USER.type)
        return Response.success()
    }

    @Operation(summary = "删除管理员")
    @SaCheckRole("SUPER_ADMIN")
    @DeleteMapping("/admin/{id}")
    fun deleteAdmin(@PathVariable("id") id: Long): Response<Unit> {
        userService.deleteByRole(id, UserTypeEnums.ADMIN_USER.type)
        return Response.success()
    }

    @Operation(summary = "获取所有普通用户")
    @SaCheckRole(value = ["SUPER_ADMIN", "ADMIN_USER"], mode = SaMode.OR)
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
}