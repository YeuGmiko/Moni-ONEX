package uno.moni.onex.business.task.pojo.domain

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import java.time.LocalDate
import java.time.LocalDateTime

data class DailyTask(
    @TableId(type = IdType.NONE)
    var id: String? = null,
    var taskDate: LocalDate? = null,
    var createTime: LocalDateTime? = null,
    var questionId: String? = null,
) {
}