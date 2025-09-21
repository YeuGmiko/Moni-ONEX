package uno.moni.onex.business.auth.service.impl

import cn.dev33.satoken.stp.StpUtil
import org.springframework.stereotype.Service
import uno.moni.onex.business.auth.pojo.dto.UserLoginBody
import uno.moni.onex.business.auth.service.LoginService
import uno.moni.onex.business.user.service.UserService
import uno.moni.onex.business.user.pojo.domain.User
import uno.moni.onex.business.auth.pojo.vo.LoginUser
import uno.moni.onex.business.user.enums.UserTypeEnums
import java.time.LocalDateTime

@Service
class LoginServiceImpl(
    var userService: UserService,
): LoginService {
    override fun login(loginBody: UserLoginBody, roles: List<String>, or: Boolean): LoginUser {
        val user = loginBody.userName?.let { userService.load(it) } ?: throw RuntimeException("用户名或密码错误")

        return buildLoginUser(user, {
            val pass = loginBody.password
            val hashPass = user.hashPassword

            val userRoles = mutableListOf<String>()
            user.userType?.let { userRoles.add(UserTypeEnums.getRoleName(it)) }
            if (or) {
                /* 只需要满足一个角色 */
                val role = roles.find { userRoles.contains(it) }
                if (role == null) throw RuntimeException("该用户无权限: $roles")
            } else {
                /* 需要满足所有角色 */
                if (!userRoles.containsAll(roles))  RuntimeException("该用户无权限: $roles")
            }
            if (pass == null || hashPass == null || !userService.validatePassword(pass, hashPass))
                return@buildLoginUser RuntimeException("用户名或密码错误")
            else
                return@buildLoginUser null
        }, { loginUser ->
            /* sa login */
            StpUtil.login(loginUser.userId, loginBody.rememberMe)
            /* set login info properties */
            loginUser.token = StpUtil.getTokenValue()
            loginUser.isRememberMe = loginBody.rememberMe
            /* set session properties */
            StpUtil.getSession(true).set("rememberMe", loginBody.rememberMe)
            /* return */
            return@buildLoginUser loginUser
        })
    }

    override fun logout() {
        StpUtil.logout()
    }

    override fun buildLoginUser(
        user: User,
        validation: () -> Exception?,
        callback: (LoginUser) -> LoginUser
    ): LoginUser {
        val exception = validation()
        if (exception != null) throw exception
        val loginUser = LoginUser()
        /* properties (not contains: tokenValue, isRememberMe) */
        loginUser.userId = user.id.toString()
        loginUser.loginTime = LocalDateTime.now()
        return callback(loginUser)
    }
}