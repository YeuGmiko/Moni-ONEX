package uno.moni.onex.business.task.pojo.domain

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDate

@TableName("daily_task_option_submit")
data class TaskSubmit(
    @TableId(type = IdType.NONE)
    var id: String? = null,
    var submitDate: LocalDate? = null,
    var userAnswer: String? = null,
    var isRight: Boolean? = null,
    var orderNo: Int? = null,
    var optionId: String? = null,
    var questionId: String? = null,
    var userId: String? = null,
    var taskId: String? = null,
) {
}