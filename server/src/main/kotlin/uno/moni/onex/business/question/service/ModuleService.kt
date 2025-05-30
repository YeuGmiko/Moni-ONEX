package uno.moni.onex.business.question.service

import uno.moni.onex.business.question.pojo.domain.ModuleDomain
import uno.moni.onex.business.question.pojo.dto.BuildModule
import uno.moni.onex.business.question.pojo.vo.ModuleVo
import uno.moni.onex.common.base.BaseService

interface ModuleService: BaseService<ModuleDomain> {
    fun getVoList(): List<ModuleVo>
    fun load(id: String): ModuleDomain?
    fun build(build: BuildModule): ModuleDomain
    fun create(build: BuildModule)
    fun update(build: BuildModule, id: String)
    fun delete(id: String)
    fun toVo(domain: ModuleDomain): ModuleVo
    /* opened */
    fun getOpenVoList(): List<ModuleVo>
}