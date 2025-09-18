package uno.moni.onex.business.user.pojo.domain

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId

data class User(
    @TableId(type = IdType.NONE)
    var id: String? = null,
    var userName: String? = null,
    var hashPassword: String? = null,
    var name: String? = null,
    var status: Int? = null,
    var userType: Int? = null,
)