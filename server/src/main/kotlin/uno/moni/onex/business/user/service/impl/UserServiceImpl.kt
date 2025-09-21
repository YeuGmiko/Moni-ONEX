package uno.moni.onex.business.user.service.impl

import cn.hutool.crypto.SecureUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import org.springframework.stereotype.Service
import uno.moni.onex.admin.pojo.dto.CreateUser
import uno.moni.onex.admin.pojo.vo.UserVo
import uno.moni.onex.business.user.enums.UserTypeEnums
import uno.moni.onex.business.user.pojo.dto.BuildUser
import uno.moni.onex.business.user.service.UserService
import uno.moni.onex.core.base.BaseServiceImpl
import uno.moni.onex.core.core.util.SecureUtils
import uno.moni.onex.business.user.mapper.UserMapper
import uno.moni.onex.business.user.pojo.domain.User
import uno.moni.onex.business.user.pojo.dto.UpdateUser
import uno.moni.onex.business.user.pojo.dto.UpdateUserAuthPassword
import uno.moni.onex.common.pojo.vo.UserCommonVo

@Service
class UserServiceImpl: UserService, BaseServiceImpl<UserMapper, User>() {
    override fun load(username: String): User? {
        val wrapper = KtQueryWrapper(User::class.java).eq(User::userName, username)
        return try {
            getOne(wrapper)
        } catch (ex:Exception) {
            null
        }
    }

    override fun load(userId: Long): User? {
        val wrapper = KtQueryWrapper(User::class.java).eq(User::id, userId)
        return try {
            getOne(wrapper)
        } catch (ex:Exception) {
            null
        }
    }

    override fun validatePassword(password: String, hashPassword: String): Boolean {
        return SecureUtil.md5(password).equals(hashPassword)
    }

    override fun build(build: BuildUser): User {
        val user = User()
        user.id = SecureUtils.generateId()
        user.userName = build.userName
        user.hashPassword = SecureUtil.md5(build.password)
        user.name = build.name
        user.userType = build.userType
        user.status = 1
        return user
    }

    override fun create(build: CreateUser, userType: UserTypeEnums) {
        if (build.userName == null) throw IllegalArgumentException("userName cannot be null")
        val existedUser = getOne(KtQueryWrapper(User::class.java).eq(User::userName, build.userName))
        if (existedUser != null) throw RuntimeException("该用户[userName=${build.userName}]已存在")
        /* build user */
        val buildUser = BuildUser()
        buildUser.userName = build.userName
        buildUser.password = build.password
        buildUser.userType = userType.type
        buildUser.name = build.name
        val user = build(buildUser)
        if (!save(user)) throw RuntimeException("创建用户失败，服务器错误")
    }

    override fun toVo(user: User): UserVo {
        val vo = UserVo()
        vo.userId = user.id.toString()
        vo.name = user.name
        vo.userName = user.userName
        vo.roles = user.userType?.let { mutableListOf(UserTypeEnums.getRoleName(it)) } ?: mutableListOf()
        return vo
    }

    override fun deleteByRole(userId: Long, userType: UserTypeEnums) {
        val wrapper = KtQueryWrapper(User::class.java).eq(User::id, userId)
        val user = getOne(wrapper)
        if (user == null) {
            throw RuntimeException("该用户[id=${userId}]不存在")
        } else if (user.userType != userType.type) {
            throw RuntimeException("无法删除用户")
        }
        if (!removeById(userId)) {
            throw RuntimeException("删除用户失败，服务器错误")
        }
    }

    override fun openCreate(build: CreateUser) {
        create(build, UserTypeEnums.COMMON_USER)
    }

    override fun loadCommon(userId: String): UserCommonVo {
        val wrapper = KtQueryWrapper(User::class.java).eq(User::id, userId)
        val user = getOne(wrapper)
        if (user == null) {
            throw RuntimeException("该用户[id=${userId}]不存在")
        }
        val vo = UserCommonVo()
        vo.userName = user.userName
        vo.name = user.name
        vo.userType = user.userType
        vo.status = user.status
        return vo
    }

    override fun updateCommon(userId: String, update: UpdateUser) {
        val wrapper = KtQueryWrapper(User::class.java).eq(User::id, userId)
        val user = getOne(wrapper)
        if (user == null) throw RuntimeException("该用户[id=$userId]不存在")
        /* update */
        user.name = update.name
        if (!updateById(user)) throw RuntimeException("用户更新信息失败")
    }

    override fun updatePassword(
        userId: String,
        update: UpdateUserAuthPassword
    ) {
        val wrapper = KtQueryWrapper(User::class.java).eq(User::id, userId)
        val user = getOne(wrapper)
        if (user == null) throw RuntimeException("该用户[id=$userId]不存在")

        /* compare password */
        val pass = update.oldPassword
        val hashPass = user.hashPassword
        if (pass == null || hashPass == null || !validatePassword(pass, hashPass))
            throw RuntimeException("密码错误，信息更改失败")
        else if (update.oldPassword.equals(update.newPassword))
            throw RuntimeException("新密码不能与旧密码相同")
        /* update */
        user.hashPassword = SecureUtil.md5(update.newPassword)
        if (!updateById(user))
            throw RuntimeException("服务器错误，信息更改失败")
    }
}