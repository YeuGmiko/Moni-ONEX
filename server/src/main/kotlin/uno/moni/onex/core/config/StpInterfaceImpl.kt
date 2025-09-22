package uno.moni.onex.core.config

import cn.dev33.satoken.model.wrapperInfo.SaDisableWrapperInfo
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

    override fun isDisabled(loginId: Any?, service: String?): SaDisableWrapperInfo? {
        val userId = loginId?.toString()?.toLong() ?: return null
        val user = userId.let { userService.load(it) } ?: return null
        return when(user.status) {
            /* 用户不可用 */
            0 -> SaDisableWrapperInfo(true, -1, 1)
            /* 用户被封禁 */
            2 -> SaDisableWrapperInfo(true, -1, 1)
            else -> null
        }
    }
}