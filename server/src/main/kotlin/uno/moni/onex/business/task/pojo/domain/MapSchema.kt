package uno.moni.onex.business.task.pojo.domain

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId

data class MapSchema(
    @TableId(type = IdType.NONE)
    var id: String? = null,
    var mapKey: String? = null,
    var mapValue: String? = null,
    var remark: String? = null
) {
}