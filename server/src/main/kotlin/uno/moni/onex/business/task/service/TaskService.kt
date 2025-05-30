package uno.moni.onex.business.task.service

import uno.moni.onex.business.question.pojo.dto.BuildQuestionOption
import uno.moni.onex.business.question.pojo.vo.QuestionOptionVo
import uno.moni.onex.business.question.pojo.vo.QuestionSubmitOptionVo
import uno.moni.onex.business.task.pojo.domain.DailyTask
import uno.moni.onex.business.task.pojo.dto.UpdateTaskConfig
import uno.moni.onex.business.task.pojo.vo.TaskRankVo
import uno.moni.onex.business.question.pojo.vo.QuestionVo
import uno.moni.onex.common.base.BaseService
import uno.moni.onex.open.vo.OpenQuestionVo
import uno.moni.onex.open.vo.OpenTaskOrderVo
import uno.moni.onex.open.vo.OpenTaskVo
import java.time.LocalDate

interface TaskService: BaseService<DailyTask> {
    fun getTaskConfig(): UpdateTaskConfig
    fun updateGenQuestionCount(newCount: Int)
    fun updateGenQuestionModules(modules: List<String>)
    fun getDailyTaskQuestions(date: LocalDate): List<QuestionVo>
    fun getDailyRank(date: LocalDate): List<TaskRankVo>
    fun genDailyTask(date: LocalDate)
//    fun postTaskQuestionAnswer(task: String, answers: List<BuildQuestionOption>, userId: String): List<QuestionOptionVo>
    /* open */
    fun toOpenVos(domains: List<DailyTask>, userId: String): List<OpenTaskVo>
    /* 提交任务答案 */
    fun postTaskAnswer(taskId: String, answers: List<BuildQuestionOption>, userId: String)
    /* 获取某天的任务题目列表 */
    fun getDailyTaskOrders(date: LocalDate, userId: String): List<OpenTaskVo>
    /* 获取任务题目信息 */
    fun getTaskQuestion(taskId: String, userId: String): OpenTaskVo
    /* 获取任务题目答案 */
    fun getTaskAnswers(taskId: String, userId: String): List<QuestionOptionVo>
    /* 获取任务提交的内容 */
    fun getTaskSubmit(taskId: String, userId: String): List<QuestionSubmitOptionVo>
}