package uno.moni.onex.business.question.service

import uno.moni.onex.business.question.pojo.domain.QuestionOption
import uno.moni.onex.business.question.pojo.dto.BuildQuestionOption
import uno.moni.onex.business.question.pojo.dto.CreateQuestionOption
import uno.moni.onex.business.question.pojo.vo.QuestionOptionVo
import uno.moni.onex.core.base.BaseService

interface QuestionOptionService: BaseService<QuestionOption> {
    fun loadByQuestionId(questionId: String): List<QuestionOptionVo>
    fun load(id: String): QuestionOption?
    fun build(create: CreateQuestionOption): QuestionOption
    fun create(build: BuildQuestionOption, questionId: String)
    fun createList(builds: List<BuildQuestionOption>, questionId: String)
    fun toVo(domain: QuestionOption): QuestionOptionVo
    fun toVos(domains: List<QuestionOption>): List<QuestionOptionVo>
}