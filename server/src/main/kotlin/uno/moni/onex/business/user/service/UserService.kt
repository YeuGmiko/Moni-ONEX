package uno.moni.onex.business.user.service

import uno.moni.onex.admin.pojo.dto.CreateUser
import uno.moni.onex.admin.pojo.vo.UserVo
import uno.moni.onex.business.user.pojo.dto.BuildUser
import uno.moni.onex.core.base.BaseService
import uno.moni.onex.business.user.pojo.domain.User

interface UserService: BaseService<User> {
    fun load(username: String): User?
    fun load(userId: Long): User?
    fun build(build: BuildUser): User
    fun create(build: CreateUser, userType: Int)
    fun toVo(user: User): UserVo
    fun validatePassword(password: String, hashPassword: String): Boolean
    fun deleteByRole(userId: Long, userType: Int)
}