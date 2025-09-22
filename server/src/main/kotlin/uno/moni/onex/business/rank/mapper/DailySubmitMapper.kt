package uno.moni.onex.business.rank.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.core.metadata.IPage
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import uno.moni.onex.business.rank.pojo.domain.DailySubmit
import uno.moni.onex.open.vo.RankRowVo
import java.time.LocalDate

@Mapper
interface DailySubmitMapper: BaseMapper<DailySubmit> {
    fun getDailyRank(page: IPage<RankRowVo>, @Param("date")date: LocalDate): IPage<RankRowVo>
    fun getTotalRank(page: IPage<RankRowVo>): IPage<RankRowVo>
}