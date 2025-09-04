package uno.moni.onex.business.question.service.impl

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import org.springframework.stereotype.Service
import uno.moni.onex.business.question.mapper.ModuleMapper
import uno.moni.onex.business.question.mapper.QuestionMapper
import uno.moni.onex.business.question.pojo.domain.ModuleDomain
import uno.moni.onex.business.question.pojo.domain.Question
import uno.moni.onex.business.question.pojo.dto.BuildModule
import uno.moni.onex.business.question.pojo.vo.ModuleVo
import uno.moni.onex.business.question.service.ModuleService
import uno.moni.onex.business.question.service.QuestionOrderService
import uno.moni.onex.core.base.BaseServiceImpl
import uno.moni.onex.core.core.util.SecureUtils
import uno.moni.onex.open.vo.ModuleOpenVo

@Service
class ModuleServiceImpl(
    private val questionMapper: QuestionMapper,
    private val moduleMapper: ModuleMapper,
    private val questionOrderService: QuestionOrderService
) : ModuleService, BaseServiceImpl<ModuleMapper, ModuleDomain>() {
    fun _getOpenVoList(userId: String?, baseWrapper: KtQueryWrapper<ModuleDomain>?): List<ModuleOpenVo> {
        val modules = moduleMapper.selectList(baseWrapper);

        return modules.map { domain ->
            val vo = ModuleOpenVo()
            vo.id = domain.id
            vo.label = domain.label
            vo.name = domain.name
            vo.bgColor = domain.bgColor
            vo.isOpen = domain.isOpen
            vo.displayOrder = domain.displayOrder
            vo.remark = domain.remark
            val orders = questionOrderService.getVoListByModuleId(userId, domain.id as String)
            vo.questions = orders
            vo.questionCount = orders.size
            return@map vo
        }
    }
    fun _getOpenVo(userId: String?, baseWrapper: KtQueryWrapper<ModuleDomain>?): ModuleOpenVo {
        val module = moduleMapper.selectOne(baseWrapper)

        val vo = ModuleOpenVo()
        vo.id = module.id
        vo.label = module.label
        vo.name = module.name
        vo.bgColor = module.bgColor
        vo.isOpen = module.isOpen
        vo.displayOrder = module.displayOrder
        vo.remark = module.remark
        val orders = questionOrderService.getVoListByModuleId(userId, module.id as String)
        vo.questions = orders
        vo.questionCount = orders.size

        return vo
    }

    override fun toVo(domain: ModuleDomain): ModuleVo {
        val questionQueryWrapper = KtQueryWrapper(Question::class.java)
        questionQueryWrapper.select(Question::id, Question::title, Question::displayOrder)
            .eq(Question::moduleId, domain.id)
        val questions = questionMapper.selectList(questionQueryWrapper)
        val vo = ModuleVo()
        vo.id = domain.id
        vo.label = domain.label
        vo.name = domain.name
        vo.bgColor = domain.bgColor
        vo.isOpen = domain.isOpen
        vo.displayOrder = domain.displayOrder
        vo.remark = domain.remark
        vo.questionCount = questions.size
        return vo
    }

    override fun toVoList(domains: List<ModuleDomain>): List<ModuleVo> {
        val questionQueryWrapper = KtQueryWrapper(Question::class.java)
        questionQueryWrapper.select(Question::id, Question::moduleId)
        val questions = questionMapper.selectList(questionQueryWrapper)
        val idMap = HashMap<String, Int>()
        for (question in questions) {
            if (question.moduleId == null) continue
            idMap[question.moduleId as String] = (idMap[question.moduleId] ?: 0) + 1
        }
        return domains.map { domain ->
            val vo = ModuleVo()
            vo.id = domain.id
            vo.label = domain.label
            vo.name = domain.name
            vo.bgColor = domain.bgColor
            vo.isOpen = domain.isOpen
            vo.displayOrder = domain.displayOrder
            vo.remark = domain.remark
            vo.questionCount = idMap[domain.id] ?: 0
            return@map vo
        }
    }

    override fun getVoList(): List<ModuleVo> {
        return toVoList(list())
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


    override fun getOpenVoList(userId: String?): List<ModuleOpenVo> {
        val queryWrapper = KtQueryWrapper(ModuleDomain::class.java).eq(ModuleDomain::isOpen, true)
        return _getOpenVoList(userId, queryWrapper)
    }
    override fun getOpenVo(userId: String?, moduleId: String): ModuleOpenVo {
        val queryWrapper = KtQueryWrapper(ModuleDomain::class.java).eq(ModuleDomain::id, moduleId)
        if (!exists(queryWrapper)) throw RuntimeException("模块[id=$moduleId]不存在")
        return _getOpenVo(userId, queryWrapper)
    }
}