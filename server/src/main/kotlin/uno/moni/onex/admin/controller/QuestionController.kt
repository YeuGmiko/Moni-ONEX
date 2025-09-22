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
import uno.moni.onex.business.question.pojo.dto.BuildQuestion
import uno.moni.onex.business.question.pojo.dto.UpdateQuestion
import uno.moni.onex.business.question.pojo.vo.QuestionOptionVo
import uno.moni.onex.business.question.pojo.vo.QuestionVo
import uno.moni.onex.business.question.service.QuestionOptionService
import uno.moni.onex.business.question.service.QuestionService
import uno.moni.onex.core.pojo.vo.Response

@SaCheckDisable
@SaCheckRole(value = ["SUPER_ADMIN", "ADMIN_USER"], mode = SaMode.OR)
@RestController("AdminQuestionController")
@RequestMapping("/admin/modules")
@Tag(name = "题库管理")
class QuestionController(
    val questionService: QuestionService,
    val questionOptionService: QuestionOptionService
) {
    @Operation(summary = "获取模块下所有题目")
    @GetMapping("/{id}/questions")
    fun getQuestions(@PathVariable id: String): Response<List<QuestionVo>> {
        return Response.Companion.success().data(questionService.loadByModuleId(id))
    }

    @Operation(summary = "获取题目信息")
    @GetMapping("/questions/{id}")
    fun getQuestion(@PathVariable id: String): Response<QuestionVo> {
        val domain = questionService.load(id)
        if (domain == null) throw RuntimeException("题目[id=${id}]不存在")
        return Response.Companion.success().data(questionService.toVo(domain))
    }

    @Transactional
    @Operation(summary = "添加题目至指定模块")
    @PostMapping("/{id}/questions")
    fun createQuestion(@PathVariable id: String, @RequestBody body: BuildQuestion): Response<Unit> {
        val questionId = questionService.create(body, id)
        questionOptionService.createList(body.options, questionId)
        return Response.Companion.success()
    }

    @Transactional
    @Operation(summary = "批量添加题目至指定模块")
    @PostMapping("/{id}/questions/batch")
    fun createQuestionBatch(@PathVariable id: String, @RequestBody body: List<BuildQuestion>): Response<Unit> {
        body.forEach { create ->
            val questionId = questionService.create(create, id)
            questionOptionService.createList(create.options, questionId)
        }
        return Response.Companion.success()
    }


    @Transactional
    @Operation(summary = "更改题目信息")
    @PutMapping("/questions/{id}")
    fun updateQuestion(@PathVariable id: String, @RequestBody body: UpdateQuestion): Response<Unit> {
        questionService.update(body, id)
        return Response.Companion.success()
    }

    @Transactional
    @Operation(summary = "删除题目")
    @DeleteMapping("/questions/{id}")
    fun deleteQuestion(@PathVariable id: String): Response<Unit> {
        questionService.delete(id)
        return Response.Companion.success()
    }

    @Operation(summary = "查询题目中所有填空项")
    @GetMapping("/questions/{id}/options")
    fun getQuestionOptions(@PathVariable id: String): Response<List<QuestionOptionVo>> {
        return Response.Companion.success().data(questionOptionService.loadByQuestionId(id))
    }
}