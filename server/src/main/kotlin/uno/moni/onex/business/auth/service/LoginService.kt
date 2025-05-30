package uno.moni.onex.business.auth.service

import uno.moni.onex.business.auth.pojo.dto.UserLoginBody
import uno.moni.onex.business.user.pojo.domain.User
import uno.moni.onex.business.auth.pojo.vo.LoginUser

interface LoginService {
    fun login(loginBody: UserLoginBody, roles: List<String>, or: Boolean): LoginUser
    fun logout()
    fun buildLoginUser(user: User, validation: () -> Exception?, callback: (loginUser: LoginUser) -> LoginUser): LoginUser
}