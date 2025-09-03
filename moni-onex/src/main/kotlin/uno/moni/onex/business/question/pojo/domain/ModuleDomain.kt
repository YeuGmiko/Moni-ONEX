package uno.moni.onex.business.question.pojo.domain

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

@TableName("module")
data class ModuleDomain(
    @TableId(type = IdType.NONE)
    var id: String? = null,
    var displayOrder: Int? = null,
    var label: String? = null,
    var name: String? = null,
    var bgColor: String? = null,
    var isOpen: Boolean? = null,
    var isGenChoose: Boolean? = null,
    var remark: String? = null
)