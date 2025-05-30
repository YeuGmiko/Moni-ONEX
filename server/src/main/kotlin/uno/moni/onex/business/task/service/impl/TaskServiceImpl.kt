package uno.moni.onex.business.task.service.impl

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import org.springframework.stereotype.Service
import uno.moni.onex.business.task.mapper.MapSchemaMapper
import uno.moni.onex.business.task.mapper.TaskMapper
import uno.moni.onex.business.task.mapper.TaskSubmitMapper
import uno.moni.onex.business.task.pojo.domain.DailyTask
import uno.moni.onex.business.task.pojo.domain.MapSchema
import uno.moni.onex.business.task.pojo.domain.TaskSubmit
import uno.moni.onex.business.task.pojo.dto.UpdateTaskConfig
import uno.moni.onex.business.task.pojo.vo.TaskRankVo
import uno.moni.onex.business.task.service.TaskService
import uno.moni.onex.business.question.mapper.ModuleMapper
import uno.moni.onex.business.question.pojo.domain.ModuleDomain
import uno.moni.onex.business.question.pojo.domain.Question
import uno.moni.onex.business.question.pojo.domain.QuestionOption
import uno.moni.onex.business.question.pojo.dto.BuildQuestionOption
import uno.moni.onex.business.question.pojo.vo.QuestionOptionVo
import uno.moni.onex.business.question.pojo.vo.QuestionSubmitOptionVo
import uno.moni.onex.business.question.pojo.vo.QuestionVo
import uno.moni.onex.business.question.service.QuestionOptionService
import uno.moni.onex.business.question.service.QuestionService
import uno.moni.onex.business.task.service.TaskSubmitService
import uno.moni.onex.business.user.enums.UserTypeEnums
import uno.moni.onex.business.user.pojo.domain.User
import uno.moni.onex.business.user.service.UserService
import uno.moni.onex.common.base.BaseServiceImpl
import uno.moni.onex.common.core.util.QuestionUtils
import uno.moni.onex.common.core.util.SecureUtils
import uno.moni.onex.common.enums.MapSchemaEnums
import uno.moni.onex.open.vo.OpenQuestionOrderVo
import uno.moni.onex.open.vo.OpenQuestionVo
import uno.moni.onex.open.vo.OpenTaskOrderVo
import uno.moni.onex.open.vo.OpenTaskVo
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class TaskServiceImpl(
    val mapMapper: MapSchemaMapper,
    val moduleMapper: ModuleMapper,
    val taskMapper: TaskMapper,
    val taskSubmitMapper: TaskSubmitMapper,
    val questionService: QuestionService,
    val userService: UserService,
    val questionOptionService: QuestionOptionService,
    private val taskSubmitService: TaskSubmitService,
): TaskService, BaseServiceImpl<TaskMapper, DailyTask>() {
    override fun getTaskConfig(): UpdateTaskConfig {
        val mapKey = MapSchemaEnums.DAILY_GEN_QUESTION_COUNT.mapKey
        val genCount = mapMapper.selectOne(KtQueryWrapper(MapSchema::class.java).eq(MapSchema::mapKey, mapKey)) ?.let { it.mapValue?.toInt() } ?: 0
        val modules = moduleMapper.selectList(KtQueryWrapper(ModuleDomain::class.java).eq(ModuleDomain::isGenChoose, true))
        val config = UpdateTaskConfig()
        config.genCount = genCount
        config.modules = modules.map{ module -> module.id ?: "" }
        return config
    }

    override fun updateGenQuestionCount(newCount: Int) {
        val mapConfig = MapSchemaEnums.DAILY_GEN_QUESTION_COUNT
        val mapWrapper = KtQueryWrapper(MapSchema::class.java).eq(MapSchema::mapKey, mapConfig.mapKey)
        var map: MapSchema? = MapSchema()
        map = mapMapper.selectOne(mapWrapper, false)
        if (map == null) {
            map = MapSchema()
        }
        map.id = map.id ?: SecureUtils.generateId()
        map.mapKey = mapConfig.mapKey
        map.mapValue = newCount.toString()
        map.remark = mapConfig.remark
        if (!mapMapper.insertOrUpdate(map)) throw RuntimeException("更新失败，服务器错误")
    }

    override fun updateGenQuestionModules(modules: List<String>) {
        /* init default value */
        val initUpdateWrapper = KtUpdateWrapper(ModuleDomain::class.java).set(ModuleDomain::isGenChoose, false)
        moduleMapper.update(initUpdateWrapper)
        /* if empty, then return, for avoid error */
        if (modules.isEmpty()) return
        /* update config */
        val updateWrapper = KtUpdateWrapper(ModuleDomain::class.java).`in`(ModuleDomain::id, modules).set(ModuleDomain::isGenChoose, true)
        val updateCount = moduleMapper.update(updateWrapper)
        if (updateCount != modules.size) throw RuntimeException("更新失败")
    }

    override fun getDailyTaskQuestions(date: LocalDate): List<QuestionVo> {
        val taskQueryWrapper = KtQueryWrapper(DailyTask::class.java).apply("DATE_FORMAT(task_date, '%y%m%d') = DATE_FORMAT({0}, '%y%m%d')", date)
        val ids = list(taskQueryWrapper).map{ task -> task.questionId }
        if (ids.isEmpty()) return emptyList()
        val questionQueryWrapper = KtQueryWrapper(Question::class.java).`in`(Question::id, ids)
        val questions = questionService.list(questionQueryWrapper)
        return questionService.toVos(questions)
    }

    override fun getDailyRank(date: LocalDate): List<TaskRankVo> {
        val submitQueryWrapper = KtQueryWrapper(TaskSubmit::class.java).apply("DATE_FORMAT(submit_date, '%y%m%d') = DATE_FORMAT({0}, '%y%m%d')", date)
        val submits = taskSubmitMapper.selectList(submitQueryWrapper)
        val questions = getDailyTaskQuestions(date)
        val users = userService.list(KtQueryWrapper(User::class.java).eq(User::userType, UserTypeEnums.COMMON_USER.type))

        val totalQuestions = questions.size
        val totalOptions = questions.fold(0) { sum, current -> sum + (current.optionsLen ?: 0) }
        /* set properties; no properties: rank */
        val rank = users.map{ user ->
            val vo = TaskRankVo()
            val userSubmitsOptions = submits.filter{ submit -> submit.userId == user.id }
            val userSubmitQuestionIds = userSubmitsOptions.map{ submit -> submit.questionId }.toHashSet()
            val userSubmitQuestions = questions.filter{ question -> userSubmitQuestionIds.contains(question.id) }

            vo.name = user.name
            vo.userName = user.userName
            vo.rightOptions = userSubmitsOptions.filter{ submit -> submit.isRight == true }.size
            vo.submitOptions = userSubmitsOptions.size
            vo.totalOptions = totalOptions
            vo.rightQuestions = userSubmitQuestions.filter{ question ->
                val options = userSubmitsOptions.filter { option -> option.questionId == question.id }
                for (submit in options) {
                    if (submit.isRight != true) false
                }
                true
            }.size
            vo.submitQuestion = userSubmitQuestions.size
            vo.totalQuestions = totalQuestions
            vo
        }
        val sorted = rank.sortedWith(compareBy(
            { -it.rightOptions!! },
            { -it.submitOptions!! },
            { -it.rightOptions!! },
            { it.name }
        ))
        sorted.forEachIndexed { index, it -> it.rank = index + 1 }
        return sorted
    }

    override fun genDailyTask(date: LocalDate) {
        /* 删除任务中已经不存在的题目 */
        val totalTaskQuestionIds = list().map { it.questionId }
        if (totalTaskQuestionIds.isNotEmpty()) {
            val existedQuestionIds = questionService.list(KtQueryWrapper(Question::class.java).`in`(Question::id, totalTaskQuestionIds)).map{ question -> question.id }
            val notExistedQuestionIds = totalTaskQuestionIds.filter { !existedQuestionIds.contains(it) }
            if (notExistedQuestionIds.isNotEmpty()) taskMapper.delete(KtQueryWrapper(DailyTask::class.java).`in`(DailyTask::questionId, notExistedQuestionIds))
        }
        /* 继续准备生成任务 */
        val taskQueryWrapper = KtQueryWrapper(DailyTask::class.java).apply("DATE_FORMAT(task_date, '%y%m%d') = DATE_FORMAT({0}, '%y%m%d')", date)
        /* 已经生成的题目 */
        val generatedQuestionIds = list(taskQueryWrapper).map{ task -> task.questionId }
        /* 生成数量指标 */
        val aimCount = mapMapper.selectOne(KtQueryWrapper(MapSchema::class.java).eq(MapSchema::mapKey, MapSchemaEnums.DAILY_GEN_QUESTION_COUNT.mapKey))?.let { it.mapValue?.toInt() } ?: 0
        /* 剩余指标 */
        val leftAimCount = aimCount - generatedQuestionIds.size
        /* 判断是否已经达到指定生成数量 */
        if (leftAimCount <= 0) return
        /* 未达成指标，继续生成 */
        val taskModuleQueryWrapper = KtQueryWrapper(ModuleDomain::class.java).eq(ModuleDomain::isGenChoose, true)
        val moduleIds = moduleMapper.selectList(taskModuleQueryWrapper).map{ module -> module.id }
        /* 开放模块为空，无法生成 */
        if (moduleIds.isEmpty()) return
        val taskQuestionQueryWrapper = KtQueryWrapper(Question::class.java).`in`(Question::moduleId, moduleIds)
        if (generatedQuestionIds.isNotEmpty()) taskQuestionQueryWrapper.notIn(Question::id, generatedQuestionIds)
        val leftGenerateQuestions = questionService.list(taskQuestionQueryWrapper.last("ORDER BY RAND() LIMIT $leftAimCount"))
        /* 添加至每日任务列表 */
        val tasksDomains = leftGenerateQuestions.map{ question ->
            val domain = DailyTask()
            domain.id = SecureUtils.generateId()
            domain.questionId = question.id
            domain.createTime = LocalDateTime.now()
            domain.taskDate = LocalDate.now()
            domain
        }
        taskMapper.insert(tasksDomains)
    }

//    override fun postTaskQuestionAnswer(
//        taskId: String,
//        answers: List<BuildQuestionOption>,
//        userId: String
//    ): List<QuestionOptionVo> {
//        val questionExisted = questionService.exists(KtQueryWrapper(Question::class.java).eq(Question::id, questionId))
//        if (!questionExisted) throw RuntimeException("题目[id=${questionId}]不存在")
//        val options = questionOptionService.list(KtQueryWrapper(QuestionOption::class.java).eq(QuestionOption::questionId, questionId))
//        val submits = options.map { option ->
//            val submit = answers.find { it.orderNo == option.orderNo }
//            if (submit == null) throw RuntimeException("找不到选项[order=${option.orderNo}]")
//            val domain = TaskSubmit()
//            domain.id = SecureUtils.generateId()
//            domain.submitDate = LocalDate.now()
//            domain.userAnswer = submit.answer
//            domain.isRight = option.answer?.equals(submit.answer) ?: false
//            domain.optionId = option.id
//            domain.questionId = questionId
//            domain.userId = userId
//            domain.taskId =
//            /* return domain */
//            domain
//        }
//        taskSubmitMapper.insert(submits)
//        return questionOptionService.toVos(options)
//    }

    override fun postTaskAnswer(
        taskId: String,
        answers: List<BuildQuestionOption>,
        userId: String
    ) {
        val task = taskMapper.selectOne(KtQueryWrapper(DailyTask::class.java).eq(DailyTask::id, taskId))
        if (task == null) throw RuntimeException("任务[id=${taskId}]不存在")
        val options = questionOptionService.list(KtQueryWrapper(QuestionOption::class.java).eq(QuestionOption::questionId, task.questionId))
        /* if empty, then return */
        if (options.isEmpty()) return
        val submits = options.map { option ->
            val submit = answers.find { it.orderNo == option.orderNo }
            if (submit == null) throw RuntimeException("找不到选项[order=${option.orderNo}]")
            val domain = TaskSubmit()
            domain.id = SecureUtils.generateId()
            domain.submitDate = LocalDate.now()
            domain.isRight = option.answer.equals(submit.answer)
            domain.userAnswer = submit.answer
            domain.orderNo = option.orderNo
            domain.optionId = option.id
            domain.questionId = task.questionId
            domain.userId = userId
            domain.taskId = task.id
            /* return */
            domain
        }
        taskSubmitMapper.insert(submits)
    }

    override fun getDailyTaskOrders(
        date: LocalDate,
        userId: String
    ): List<OpenTaskVo> {
        val taskWrapper = KtQueryWrapper(DailyTask::class.java).apply("DATE_FORMAT(task_date, '%y%m%d') = DATE_FORMAT({0}, '%y%m%d')", date)
        val tasks = taskMapper.selectList(taskWrapper)
        return toOpenVos(tasks, userId)
    }

    override fun toOpenVos(domains: List<DailyTask>, userId: String): List<OpenTaskVo> {
        val taskIds = domains.map{ it.id }
        val questionIds = domains.map { it.questionId }
        if (questionIds.isEmpty()) return emptyList()

        val questions = questionService.list(KtQueryWrapper(Question::class.java).`in`(Question::id, questionIds))
        val moduleIds = questions.map { it.moduleId }
        if (moduleIds.isEmpty()) return emptyList()

        val userSubmits = taskSubmitMapper.selectList(
            KtQueryWrapper(TaskSubmit::class.java).`in`(TaskSubmit::taskId, taskIds)
        )
        val questionOptions = questionOptionService.list(
            KtQueryWrapper(QuestionOption::class.java).`in`(
                QuestionOption::questionId,
                questionIds
            )
        )
        val modules =
            moduleMapper.selectList(KtQueryWrapper(ModuleDomain::class.java).`in`(ModuleDomain::id, moduleIds))

        /* 使用Map便于快速查询信息 */
        val questionMap = mutableMapOf<String, Question>()
        questions.forEach { question ->
            val questionId = question.id
            if (questionId != null) questionMap[questionId] = question
        }
        val moduleMap = mutableMapOf<String, ModuleDomain>()
        modules.forEach { module ->
            val moduleId = module.id
            if (moduleId != null) moduleMap[moduleId] = module
        }

        val vos = domains.map { domain ->
            val question = questionMap[domain.questionId]
            val module = question?.let { moduleMap[it.moduleId] }

            val vo = OpenTaskVo()
            vo.id = domain.id
            vo.createTime = domain.createTime

            if (question != null && module != null) {
                val answers = questionOptions.filter { it.questionId == question.id }
                val submits = userSubmits.filter { it.userId == userId && it.questionId == question.id }
                if (submits.isNotEmpty() && answers.size != submits.size) throw RuntimeException("用户[id=${userId}]在题目[${question.title}]中有未提交的选项")
                vo.title = question.title
                vo.content = question.content
                vo.displayOrder = question.displayOrder
                vo.optionsLen = answers.size
                vo.moduleId = module.id
                vo.moduleName = module.name
                vo.moduleLabel = module.label
                vo.themeColor = module.bgColor
                vo.accomplishStatus = QuestionUtils.getAccomplishStatus(
                    answers.map { it.answer ?: "" },
                    submits.map { it.userAnswer ?: "" })
                vo.userSubmits = submits.map { taskSubmitService.toBaseVo(it) }
                /* return */
                vo
            } else vo // return
        }
        return vos
    }

    private fun toOrders(tasks: List<OpenTaskVo>): List<OpenTaskOrderVo> {
        return tasks.map { question ->
            val vo = OpenTaskOrderVo()
            vo.id = question.id
            vo.title = question.title
            vo.accomplishStatus = question.accomplishStatus
            vo.displayOrder = question.displayOrder
            /* return */
            vo
        }
    }

    override fun getTaskQuestion(taskId: String, userId: String): OpenTaskVo {
        val task = getOne(KtQueryWrapper(DailyTask::class.java).eq(DailyTask::id, taskId))
        if (task == null) throw RuntimeException("任务[id=${taskId}]不存在")
        val vo: OpenTaskVo? = toOpenVos(listOf(task), userId).first()
        if (vo == null) throw RuntimeException("题目[id=${task.questionId}]不存在")
        return vo
    }

    override fun getTaskAnswers(
        taskId: String,
        userId: String
    ): List<QuestionOptionVo> {
        val task = getOne(KtQueryWrapper(DailyTask::class.java).eq(DailyTask::id, taskId))
        if (task == null) throw RuntimeException("任务[id=${taskId}]不存在")
        val question = questionService.getOne(KtQueryWrapper(Question::class.java).eq(Question::id, task.questionId))
        if (question == null || question.id == null) throw RuntimeException("题目[id=${task.questionId}]不存在")
        val taskSubmitWrapper = KtQueryWrapper(TaskSubmit::class.java).eq(TaskSubmit::taskId, taskId).eq(TaskSubmit::userId, userId)
        if (!taskSubmitMapper.exists(taskSubmitWrapper)) throw RuntimeException("用户未提交，不允许查看答案")
        return questionOptionService.loadByQuestionId(question.id!!)
    }

    override fun getTaskSubmit(
        taskId: String,
        userId: String
    ): List<QuestionSubmitOptionVo> {
        val task = getOne(KtQueryWrapper(DailyTask::class.java).eq(DailyTask::id, taskId))
        if (task == null) throw RuntimeException("任务[id=${taskId}]不存在")
        val submits = taskSubmitService.list(KtQueryWrapper(TaskSubmit::class.java).eq(TaskSubmit::taskId, taskId).eq(
            TaskSubmit::userId, userId))
        return submits.map { taskSubmitService.toBaseVo(it) }
    }
}
