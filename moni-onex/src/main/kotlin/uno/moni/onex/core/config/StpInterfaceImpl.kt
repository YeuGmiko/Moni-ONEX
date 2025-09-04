package uno.moni.onex.core.config

import cn.dev33.satoken.stp.StpInterface
import org.springframework.stereotype.Component
import uno.moni.onex.business.user.enums.UserTypeEnums
import uno.moni.onex.business.user.service.UserService

@Component
class StpInterfaceImpl(
    var userService: UserService
): StpInterface {
    override fun getPermissionList(
        loginId: Any?,
        loginType: String?
    ): List<String?>? {
        return emptyList()
    }

    override fun getRoleList(
        loginId: Any?,
        loginType: String?
    ): List<String?>? {
        val userId = loginId?.toString()?.toLong() ?: return null
        val roles = mutableListOf<String>()
        val user = userId.let { userService.load(it) } ?: return emptyList()
        user.userType?.let { roles.add(UserTypeEnums.getRoleName(it)) }
        return roles
    }
}