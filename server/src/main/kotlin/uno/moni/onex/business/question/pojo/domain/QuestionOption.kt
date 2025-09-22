package uno.moni.onex.business.question.pojo.domain

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

@TableName("question_option")
data class QuestionOption(
    @TableId(type = IdType.NONE)
    var id: String? = null,
    var orderNo: Int? = null,
    var answer: String? = null,
    var questionId: String? = null
)