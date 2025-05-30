package uno.moni.onex.business.question.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import uno.moni.onex.business.question.pojo.domain.QuestionOption

@Mapper
interface QuestionOptionMapper: BaseMapper<QuestionOption> {
}