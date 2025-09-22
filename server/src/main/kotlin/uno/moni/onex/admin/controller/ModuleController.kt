package uno.moni.onex.admin.controller

import cn.dev33.satoken.annotation.SaCheckDisable
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.annotation.SaMode
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uno.moni.onex.business.question.pojo.dto.BuildModule
import uno.moni.onex.business.question.pojo.vo.ModuleVo
import uno.moni.onex.business.question.service.ModuleService
import uno.moni.onex.core.pojo.vo.Response

@SaCheckDisable
@SaCheckRole(value = ["SUPER_ADMIN", "ADMIN_USER"], mode = SaMode.OR)
@RestController("AdminModuleController")
@RequestMapping("/admin/modules")
@Tag(name = "题库模块管理")
class ModuleController(
    val moduleService: ModuleService
) {
    @Operation(summary = "获取模块列表")
    @GetMapping
    fun getModules(): Response<List<ModuleVo>> {
        return Response.Companion.success().data(moduleService.getVoList())
    }

    @Operation(summary = "获取模块信息")
    @GetMapping("/{id}")
    fun getModule(@PathVariable id: String): Response<ModuleVo> {
        val domain = moduleService.load(id)
        if (domain == null) throw RuntimeException("该模块[id=${id}]不存在")
        return Response.Companion.success().data(moduleService.toVo(domain))
    }

    @Transactional
    @Operation(summary = "添加模块")
    @PostMapping
    fun createModule(@RequestBody body: BuildModule): Response<Unit> {
        return Response.Companion.success().data(moduleService.create(body))
    }

    @Transactional
    @Operation(summary = "更新模块信息")
    @PutMapping("/{id}")
    fun updateModule(@RequestBody body: BuildModule, @PathVariable id: String): Response<Unit> {
        moduleService.update(body, id)
        return Response.Companion.success()
    }

    @Transactional
    @Operation(summary = "删除模块")
    @DeleteMapping("/{id}")
    fun deleteModule(@PathVariable id: String): Response<Unit> {
        moduleService.delete(id)
        return Response.Companion.success()
    }
}