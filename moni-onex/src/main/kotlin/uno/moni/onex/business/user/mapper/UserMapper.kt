package uno.moni.onex.business.user.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import uno.moni.onex.business.user.pojo.domain.User

@Mapper
interface UserMapper: BaseMapper<User> {
}