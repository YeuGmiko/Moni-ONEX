package uno.moni.onex.business.question.pojo.domain

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDate

@TableName("question_submit_option")
class QuestionSubmitOption(
    @TableId(type = IdType.NONE)
    var id: String? = null,
    var submitDate: LocalDate? = null,
    var userAnswer: String? = null,
    var optionId: String? = null,
    var questionId: String? = null,
    var userId: String? = null,
)