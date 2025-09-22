package uno.moni.onex.business.rank.service.impl

import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import org.springframework.stereotype.Service
import uno.moni.onex.business.rank.mapper.DailySubmitMapper
import uno.moni.onex.business.rank.pojo.domain.DailySubmit
import uno.moni.onex.business.rank.service.RankService
import uno.moni.onex.core.base.BaseServiceImpl
import uno.moni.onex.open.vo.RankRowVo
import java.time.LocalDate

@Service
class RankServiceImpl(
    val dailySubmitMapper: DailySubmitMapper
): RankService, BaseServiceImpl<DailySubmitMapper, DailySubmit>() {
    override fun getDailyRankPage(
        page: Long,
        size: Long,
        date: LocalDate
    ): IPage<RankRowVo> {
        val voPage = Page<RankRowVo>(page, size)
        val rank = dailySubmitMapper.getDailyRank(voPage, date)
        return rank
    }

    override fun getTotalRankPage(
        page: Long,
        size: Long
    ): IPage<RankRowVo> {
        val voPage = Page<RankRowVo>(page, size)
        val rank = dailySubmitMapper.getTotalRank(voPage)
        return rank
    }
}