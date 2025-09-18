package uno.moni.onex.business.question.service

import uno.moni.onex.business.question.pojo.domain.ModuleDomain
import uno.moni.onex.business.question.pojo.dto.BuildModule
import uno.moni.onex.business.question.pojo.vo.ModuleVo
import uno.moni.onex.core.base.BaseService
import uno.moni.onex.open.vo.ModuleOpenVo

interface ModuleService: BaseService<ModuleDomain> {
    /* admin */
    fun toVo(domain: ModuleDomain): ModuleVo
    fun toVoList(domains: List<ModuleDomain>): List<ModuleVo>
    fun getVoList(): List<ModuleVo>
    fun load(id: String): ModuleDomain?
    fun create(build: BuildModule)
    fun update(build: BuildModule, id: String)
    fun delete(id: String)
    /* opened */
    fun getOpenVoList(userId: String?): List<ModuleOpenVo>
    fun getOpenVo(userId: String?, moduleId: String): ModuleOpenVo
}