package uno.moni.onex.open.controller

import cn.dev33.satoken.annotation.SaCheckDisable
import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.core.metadata.IPage
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uno.moni.onex.business.question.pojo.dto.BuildQuestionOption
import uno.moni.onex.business.question.pojo.vo.QuestionOptionVo
import uno.moni.onex.business.question.pojo.vo.QuestionSubmitOptionVo
import uno.moni.onex.business.question.service.QuestionService
import uno.moni.onex.core.pojo.vo.Response
import uno.moni.onex.open.vo.OpenQuestionVo
import uno.moni.onex.open.vo.QuestionOrderOpenVo

@RestController
@RequestMapping("/modules")
@Tag(name = "题目管理（用户端）")
class QuestionController(
    val questionService: QuestionService,
) {

    @Operation(summary = "获取模块下题序（分页查询）")
    @GetMapping("/{moduleId}/questions/orders")
    fun getQuestionOrders(
        @PathVariable moduleId: String,
        @RequestParam("current", required = false, defaultValue = "1") current: Long,
        @RequestParam("size", required = false, defaultValue = "10") size: Long
    ): Response<IPage<QuestionOrderOpenVo>> {
        val userId = try {
            StpUtil.getLoginId().toString()
        } catch(e: Exception) {
            null
        }
        return Response.success().data(questionService.loadOrdersByModuleId(current, size, moduleId, userId))
    }

    @SaCheckLogin
    @Operation(summary = "获取题目信息")
    @GetMapping("/questions/{questionId}")
    fun getQuestion(@PathVariable questionId: String): Response<OpenQuestionVo> {
        val userId = StpUtil.getLoginId().toString()
        return Response.success().data(questionService.loadOpenById(questionId, userId))
    }

    @SaCheckDisable
    @SaCheckLogin
    @Operation(summary = "获取题目答案")
    @GetMapping("/questions/{questionId}/answers")
    fun getQuestionAnswers(@PathVariable questionId: String): Response<List<QuestionOptionVo>> {
        val userId = StpUtil.getLoginId().toString()
        return Response.success().data(questionService.loadQuestionAnswers(questionId, userId))
    }

    @SaCheckDisable
    @SaCheckLogin
    @Operation(summary = "获取题目的提交内容")
    @GetMapping("/questions/{questionId}/submits")
    fun getQuestionSubmits(@PathVariable questionId: String): Response<List<QuestionSubmitOptionVo>> {
        val userId = StpUtil.getLoginId().toString()
        return Response.success().data(questionService.loadQuestionSubmits(questionId, userId))
    }

    @SaCheckDisable
    @SaCheckLogin
    @Operation(summary = "提交题目答案")
    @PostMapping("/questions/{questionId}/submits")
    fun postQuestionAnswer(@PathVariable questionId: String, @RequestBody answers: List<BuildQuestionOption>): Response<List<QuestionOptionVo>> {
        val userId = StpUtil.getLoginId().toString()
        return Response.success().data(questionService.postQuestionSubmits(questionId, answers, userId))
    }
}