package uno.moni.onex.open.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uno.moni.onex.business.question.pojo.vo.ModuleVo
import uno.moni.onex.business.question.service.ModuleService
import uno.moni.onex.common.pojo.vo.Response

@RestController
@RequestMapping("/modules")
@Tag(name = "模块管理（用户端）")
class ModulesController(
    val moduleService: ModuleService
) {
    @Operation(summary = "获取公开的模块列表")
    @GetMapping
    fun getModules(): Response<List<ModuleVo>> {
        return Response.Companion.success().data(moduleService.getOpenVoList())
    }
    @Operation(summary = "获取模块信息")
    @GetMapping("/{id}")
    fun getModule(@PathVariable id: String): Response<ModuleVo> {
        val domain = moduleService.load(id)
        if (domain == null) throw RuntimeException("模块[id=$id]不存在")
        return Response.Companion.success().data(moduleService.toVo(domain))
    }
}