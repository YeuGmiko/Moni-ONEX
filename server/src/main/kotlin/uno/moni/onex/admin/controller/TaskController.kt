package uno.moni.onex.admin.controller

import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.annotation.SaMode
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uno.moni.onex.business.question.pojo.vo.QuestionVo
import uno.moni.onex.business.task.pojo.dto.UpdateTaskConfig
import uno.moni.onex.business.task.pojo.vo.TaskRankVo
import uno.moni.onex.business.task.service.TaskService
import uno.moni.onex.common.pojo.vo.Response
import java.time.LocalDate

@RestController("AdminTaskController")
@RequestMapping("/admin/task")
@Tag(name = "每日任务管理")
class TaskController(
    val taskService: TaskService
) {

    @SaCheckRole(value = ["SUPER_ADMIN", "ADMIN_USER"], mode = SaMode.OR)
    @Operation(summary = "获取每日任务配置参数")
    @GetMapping("/config")
    fun getTaskConfig(): Response<UpdateTaskConfig> {
        return Response.Companion.success().data(taskService.getTaskConfig())
    }

    @Transactional
    @SaCheckRole(value = ["SUPER_ADMIN", "ADMIN_USER"], mode = SaMode.OR)
    @Operation(summary = "修改每日任务配置参数")
    @PutMapping("/config")
    fun updateTaskConfig(@RequestBody body: UpdateTaskConfig): Response<Unit> {
        val genCount = body.genCount
        val modules = body.modules
        require(genCount != null && genCount > 0) { "生成数量必须大于0" }
        taskService.updateGenQuestionCount(genCount)
        taskService.updateGenQuestionModules(modules)
        return Response.Companion.success()
    }

    @Transactional
    @SaCheckRole(value = ["SUPER_ADMIN", "ADMIN_USER"], mode = SaMode.OR)
    @Operation(summary = "手动生成今日任务题目列表")
    @PutMapping("/questions")
    fun updateTaskQuestions(): Response<Unit> {
        taskService.genDailyTask(LocalDate.now())
        return Response.Companion.success()
    }


    @SaCheckRole(value = ["SUPER_ADMIN", "ADMIN_USER"], mode = SaMode.OR)
    @Operation(summary = "获取今日任务题目列表")
    @GetMapping("/questions")
    fun getTaskQuestions(): Response<List<QuestionVo>> {
        val date = LocalDate.now()
        return Response.Companion.success().data(taskService.getDailyTaskQuestions(date))
    }

    @SaCheckRole(value = ["SUPER_ADMIN", "ADMIN_USER"], mode = SaMode.OR)
    @Operation(summary = "获取排行榜（每日）")
    @GetMapping("/rank/daily")
    fun getRankDaily(): Response<List<TaskRankVo>> {
        val date = LocalDate.now()
        return Response.Companion.success().data(taskService.getDailyRank(date))
    }
}