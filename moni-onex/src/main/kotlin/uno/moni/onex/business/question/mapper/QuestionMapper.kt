package uno.moni.onex.business.question.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import uno.moni.onex.business.question.pojo.domain.Question

@Mapper
interface QuestionMapper: BaseMapper<Question> {
    fun getIdsByModuleId(@Param("moduleId")moduleId: String): List<String>
}