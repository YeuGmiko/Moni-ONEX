package uno.moni.onex.open.controller

import cn.dev33.satoken.stp.StpUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uno.moni.onex.business.question.service.ModuleService
import uno.moni.onex.core.pojo.vo.Response
import uno.moni.onex.open.vo.ModuleOpenVo

@RestController
@RequestMapping("/modules")
@Tag(name = "模块管理（用户端）")
class ModulesController(
    val moduleService: ModuleService
) {
    @Operation(summary = "获取公开的模块列表")
    @GetMapping
    fun getModules(): Response<List<ModuleOpenVo>> {
        val userId = try {
            StpUtil.getLoginId().toString()
        } catch(e: Exception) {
            null
        }
        return Response.Companion.success().data(moduleService.getOpenVoList(userId))
    }

    @Operation(summary = "获取模块信息")
    @GetMapping("/{id}")
    fun getModule(@PathVariable id: String): Response<ModuleOpenVo> {
        val userId = try {
            StpUtil.getLoginId().toString()
        } catch(e: Exception) {
            null
        }
        return Response.Companion.success().data(moduleService.getOpenVo(userId, id))
    }
}