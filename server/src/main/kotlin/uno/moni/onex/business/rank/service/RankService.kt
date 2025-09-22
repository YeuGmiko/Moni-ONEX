package uno.moni.onex.business.rank.service

import com.baomidou.mybatisplus.core.metadata.IPage
import uno.moni.onex.business.rank.pojo.domain.DailySubmit
import uno.moni.onex.core.base.BaseService
import uno.moni.onex.open.vo.RankRowVo
import java.time.LocalDate

interface RankService: BaseService<DailySubmit> {
    fun getDailyRankPage(page: Long, size: Long, date: LocalDate): IPage<RankRowVo>
    fun getTotalRankPage(page: Long, size: Long): IPage<RankRowVo>
}