package uno.moni.onex.business.question.pojo.domain

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import java.time.LocalDateTime

data class Question(
    @TableId(type = IdType.NONE)
    var id: String? = null,
    var title: String? = null,
    var content: String? = null,
    var displayOrder: Int? = null,
    var createTime: LocalDateTime? = null,
    var moduleId: String? = null,
)