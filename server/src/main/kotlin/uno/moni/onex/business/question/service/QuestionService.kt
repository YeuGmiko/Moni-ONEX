package uno.moni.onex.business.question.service

import uno.moni.onex.business.question.pojo.domain.Question
import uno.moni.onex.business.question.pojo.domain.QuestionOption
import uno.moni.onex.business.question.pojo.dto.BuildQuestion
import uno.moni.onex.business.question.pojo.dto.BuildQuestionOption
import uno.moni.onex.business.question.pojo.dto.CreateQuestion
import uno.moni.onex.business.question.pojo.dto.UpdateQuestion
import uno.moni.onex.business.question.pojo.vo.QuestionOptionVo
import uno.moni.onex.business.question.pojo.vo.QuestionSubmitOptionVo
import uno.moni.onex.business.question.pojo.vo.QuestionVo
import uno.moni.onex.core.base.BaseService
import uno.moni.onex.open.vo.OpenQuestionVo
import uno.moni.onex.open.vo.QuestionOrderOpenVo

interface QuestionService: BaseService<Question> {
    fun loadByModuleId(moduleId: String): List<QuestionVo>
    fun load(id: String): Question?
    fun build(build: CreateQuestion): Question
    fun create(build: BuildQuestion, moduleId: String): String
    fun update(update: UpdateQuestion, id: String)
    fun delete(id: String)
    fun toVo(domain: Question): QuestionVo
    fun toVos(domains: Collection<Question>): List<QuestionVo>
    fun toSubmitOptionVos(domains: Collection<QuestionOption>): List<QuestionOptionVo>
    /* opened */
//    fun toOpenVos(domains: Collection<Question>, userId: String?): List<OpenQuestionVo>
//    fun loadOpenedByModuleId(moduleId: String, userId: String?): List<OpenQuestionVo>
    fun toOpenVo(userId: String?, domain: Question): OpenQuestionVo
    fun loadOpenById(id: String, userId: String): OpenQuestionVo
    fun loadOrdersByModuleId(moduleId: String, userId: String?): List<QuestionOrderOpenVo>
    fun postQuestionSubmits(questionId: String, answers: List<BuildQuestionOption>, userId: String): List<QuestionOptionVo>
    fun loadQuestionSubmits(questionId: String, userId: String): List<QuestionSubmitOptionVo>
    fun loadQuestionAnswers(questionId: String, userId: String): List<QuestionOptionVo>
}