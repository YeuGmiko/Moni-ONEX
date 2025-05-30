package uno.moni.onex.business.question.service.impl

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import org.springframework.stereotype.Service
import uno.moni.onex.business.question.mapper.ModuleMapper
import uno.moni.onex.business.question.pojo.domain.ModuleDomain
import uno.moni.onex.business.question.pojo.dto.BuildModule
import uno.moni.onex.business.question.pojo.vo.ModuleVo
import uno.moni.onex.business.question.service.ModuleService
import uno.moni.onex.common.base.BaseServiceImpl
import uno.moni.onex.common.core.util.SecureUtils

@Service
class ModuleServiceImpl: ModuleService, BaseServiceImpl<ModuleMapper, ModuleDomain>() {
    override fun getVoList(): List<ModuleVo> {
        return list().map { domain ->
            toVo(domain)
        }.toList()
    }

    override fun load(id: String): ModuleDomain? {
        val wrapper = KtQueryWrapper(ModuleDomain::class.java).eq(ModuleDomain::id, id)
        return try {
            getOne(wrapper)
        } catch (e :Exception) {
            null
        }
    }

    override fun build(build: BuildModule): ModuleDomain {
        val domain = ModuleDomain()
        domain.id = SecureUtils.generateId()
        domain.label = build.label
        domain.name = build.name
        domain.displayOrder = build.order
        domain.bgColor = build.bgColor
        domain.remark = build.remark
        return domain
    }

    override fun create(buildBody: BuildModule) {
        val existed = exists(KtQueryWrapper(ModuleDomain::class.java).eq(ModuleDomain::name, buildBody.name))
        if (existed) throw RuntimeException("该模块名称[${buildBody.name}]已存在")
        /* build module */
        val module = build(buildBody)
        /* default value */
        module.isOpen = true
        module.isGenChoose = true
        if (!save(module)) throw RuntimeException("创建模块失败，服务器错误")
    }

    override fun update(build: BuildModule, id: String) {
        val wrapper = KtQueryWrapper(ModuleDomain::class.java).eq(ModuleDomain::id, id)
        val module = getOne(wrapper)
        if (module == null) throw RuntimeException("该模块[id=${id}]不存在")
        module.label = build.label
        module.name = build.name
        module.displayOrder = build.order
        module.bgColor = build.bgColor
        module.remark = build.remark
        module.isOpen = build.isOpen
        if (!updateById(module)) throw RuntimeException("更新失败，服务器错误")
    }

    override fun delete(id: String) {
        val wrapper = KtQueryWrapper(ModuleDomain::class.java).eq(ModuleDomain::id, id)
        if (!exists(wrapper)) throw RuntimeException("该模块[id=${id}]不存在")
        remove(wrapper)
    }

    override fun toVo(domain: ModuleDomain): ModuleVo {
        val vo = ModuleVo()
        vo.id = domain.id
        vo.label = domain.label
        vo.name = domain.name
        vo.bgColor = domain.bgColor
        vo.isOpen = domain.isOpen
        vo.displayOrder = domain.displayOrder
        vo.remark = domain.remark
        return vo
    }

    override fun getOpenVoList(): List<ModuleVo> {
        val wrapper = KtQueryWrapper(ModuleDomain::class.java).eq(ModuleDomain::isOpen, true)
        return list(wrapper).map { domain ->
            toVo(domain)
        }.toList()
    }
}