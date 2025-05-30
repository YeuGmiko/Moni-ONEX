package uno.moni.onex.open.controller

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.stp.StpUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uno.moni.onex.business.question.pojo.dto.BuildQuestionOption
import uno.moni.onex.business.question.pojo.vo.QuestionOptionVo
import uno.moni.onex.business.question.pojo.vo.QuestionSubmitOptionVo
import uno.moni.onex.business.task.service.TaskService
import uno.moni.onex.common.pojo.vo.Response
import uno.moni.onex.open.vo.OpenTaskOrderVo
import uno.moni.onex.open.vo.OpenTaskVo
import java.time.LocalDate

@RestController
@RequestMapping("/task")
@Tag(name = "任务管理（用户端）")
class TaskController(
    val taskService: TaskService
) {
    @SaCheckLogin
    @Operation(summary = "获取今日任务题目列表（用于生成题序）")
    @GetMapping("/orders")
    fun getTaskQuestions(): Response<List<OpenTaskVo>> {
        val userId = StpUtil.getLoginId().toString()
        val date = LocalDate.now()
        return Response.success().data(taskService.getDailyTaskOrders(date, userId))
    }

    @SaCheckLogin
    @Operation(summary = "获取任务题目信息")
    @GetMapping("/{taskId}")
    fun getTaskQuestion(@PathVariable taskId: String): Response<OpenTaskVo> {
        val userId = StpUtil.getLoginId().toString()
        return Response.success().data(taskService.getTaskQuestion(taskId, userId))
    }

    @SaCheckLogin
    @Operation(summary = "获取任务题目答案")
    @GetMapping("/{taskId}/answers")
    fun getTaskAnswers(@PathVariable taskId: String): Response<List<QuestionOptionVo>> {
        val userId = StpUtil.getLoginId().toString()
        return Response.success().data(taskService.getTaskAnswers(taskId, userId))
    }

    @SaCheckLogin
    @Operation(summary = "获取任务的提交内容")
    @GetMapping("/{taskId}/submits")
    fun getTaskSubmits(@PathVariable taskId: String): Response<List<QuestionSubmitOptionVo>> {
        val userId = StpUtil.getLoginId().toString()
        return Response.success().data(taskService.getTaskSubmit(taskId, userId))
    }

    @Transactional
    @SaCheckLogin
    @SaCheckRole("COMMON_USER")
    @Operation(summary = "提交任务题目答案")
    @PostMapping("/{taskId}/submits")
    fun postTaskQuestionAnswer(@PathVariable taskId: String, @RequestBody answers: List<BuildQuestionOption>): Response<Unit> {
        val userId = StpUtil.getLoginId().toString()
        taskService.postTaskAnswer(taskId, answers, userId)
        return Response.success()
    }
}