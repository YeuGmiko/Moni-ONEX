package uno.moni.onex.business.task.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import uno.moni.onex.business.task.pojo.domain.TaskSubmit

@Mapper
interface TaskSubmitMapper: BaseMapper<TaskSubmit> {
}