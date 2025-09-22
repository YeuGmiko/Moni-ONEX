package uno.moni.onex.open.controller

import com.baomidou.mybatisplus.core.metadata.IPage
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uno.moni.onex.business.rank.service.RankService
import uno.moni.onex.core.pojo.vo.Response
import uno.moni.onex.open.vo.RankRowVo
import java.time.LocalDate

@RestController
@RequestMapping("/rank")
@Tag(name = "用户排名模块")
class RankController(
    val rankService: RankService
) {
    @Operation(summary = "指定日期排名")
    @GetMapping("/{date}")
    fun getDailyRank(
        @PathVariable("date") date: LocalDate,
        @RequestParam("current", required = false, defaultValue = "1") current: Long,
        @RequestParam("size", required = false, defaultValue = "10") size: Long
    ): Response<IPage<RankRowVo>> {
        return Response.Companion.success().data(rankService.getDailyRankPage(current, size, date))
    }

    @Operation(summary = "总排名")
    @GetMapping
    fun getTotalRank(
        @RequestParam("current", required = false, defaultValue = "1") current: Long,
        @RequestParam("size", required = false, defaultValue = "10") size: Long
    ): Response<IPage<RankRowVo>> {
        return Response.Companion.success().data(rankService.getTotalRankPage(current, size))
    }
}