package uno.moni.onex.business.user.service

import uno.moni.onex.admin.pojo.dto.CreateUser
import uno.moni.onex.admin.pojo.vo.UserVo
import uno.moni.onex.business.user.enums.UserTypeEnums
import uno.moni.onex.business.user.pojo.dto.BuildUser
import uno.moni.onex.core.base.BaseService
import uno.moni.onex.business.user.pojo.domain.User
import uno.moni.onex.business.user.pojo.dto.UpdateUser
import uno.moni.onex.business.user.pojo.dto.UpdateUserAuthPassword
import uno.moni.onex.common.pojo.vo.UserCommonVo

interface UserService: BaseService<User> {
    fun loadByUserName(username: String): User?
    fun load(userId: String): User?
    fun build(build: BuildUser): User
    fun create(build: CreateUser, userType: UserTypeEnums)
    fun toVo(user: User): UserVo
    fun validatePassword(password: String, hashPassword: String): Boolean
    fun deleteByRole(userId: String, userType: UserTypeEnums)
    fun changeBanned(operatorUserId: String, sufferUserId: String, banned: Boolean)
    fun updateUser(operatorUserId: String, sufferUserId: String, update: uno.moni.onex.admin.pojo.dto.UpdateUser)
    /* open */
    fun openCreate(build: CreateUser)
    /* common */
    fun loadCommon(userId: String): UserCommonVo
    fun updateCommon(userId: String, update: UpdateUser)
    fun updatePassword(userId: String, update: UpdateUserAuthPassword)
}