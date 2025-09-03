package uno.moni.onex.business.question.service.impl

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import org.springframework.stereotype.Service
import uno.moni.onex.business.question.mapper.QuestionMapper
import uno.moni.onex.business.question.mapper.QuestionOptionMapper
import uno.moni.onex.business.question.mapper.QuestionSubmitOptionMapper
import uno.moni.onex.business.question.pojo.domain.Question
import uno.moni.onex.business.question.pojo.domain.QuestionOption
import uno.moni.onex.business.question.pojo.domain.QuestionSubmitOption
import uno.moni.onex.open.vo.QuestionOrderOpenVo
import uno.moni.onex.business.question.service.QuestionOrderService

@Service
class QuestionOrderServiceImpl(
    private val questionMapper: QuestionMapper,
    private val questionOptionMapper: QuestionOptionMapper,
    private val questionSubmitOptionMapper: QuestionSubmitOptionMapper
): QuestionOrderService {
    override fun calculateAccomplishStatus(
        submits: List<QuestionSubmitOption>,
        answers: List<QuestionOption>
    ): Int {
        if (submits.isEmpty() && !answers.isEmpty()) {
            return 0
        }
        val submitMap = HashMap<String, QuestionSubmitOption>()
        for (submitOption in submits) {
            submitOption.optionId?.let { submitMap[it] = submitOption }
        }
        var accomplishStatus = 2
        /* 判断是否有错误答案 value=1 */
        for (answer in  answers) {
            val submit = submitMap[answer.id]
            if (submit == null || answer.answer != submit.userAnswer) {
                accomplishStatus = 1
                break
            }
        }
        return accomplishStatus
    }

    override fun getVoListByModuleId(userId: String?, moduleId: String): List<QuestionOrderOpenVo> {
        val questionQueryWrapper = KtQueryWrapper(Question::class.java).eq(Question::moduleId, moduleId)
        val questions = questionMapper.selectList(questionQueryWrapper)
        val questionIds = questions.map { it.id }

        /* 用户未登录 */
        if (userId == null) {
            return questions.map{ question ->
                val vo = QuestionOrderOpenVo()
                vo.id = question.id
                vo.title = question.title
                vo.displayOrder = question.displayOrder
                vo.accomplishStatus = 0
                return@map vo
            }
        }

        /* 慎防SQL IN查询列表为空！！！*/
        if (questionIds.isEmpty()) return emptyList()
        val questionOptionsQueryWrapper = KtQueryWrapper(QuestionOption::class.java).`in`(QuestionOption::questionId, questionIds)
        val questionOptions = questionOptionMapper.selectList(questionOptionsQueryWrapper)

        val questionSubmitQueryWrapper = KtQueryWrapper(QuestionSubmitOption::class.java).eq(QuestionSubmitOption::userId, userId).`in`(QuestionSubmitOption::questionId, questionIds)
        val questionSubmitOptions = questionSubmitOptionMapper.selectList(questionSubmitQueryWrapper)

        val optionsMap = HashMap<String, MutableList<QuestionOption>>()
        val submitsMap = HashMap<String, MutableList<QuestionSubmitOption>>()

        questionOptions.forEach{ option ->
            var options = optionsMap[option.questionId] ?: ArrayList()
            if (option == null) {
                options = ArrayList()
            }
            options.add(option)
            optionsMap[option.questionId as String] = options
        }
        questionSubmitOptions.forEach{ option ->
            var options = submitsMap[option.questionId] ?: ArrayList()
            if (option == null) {
                options = ArrayList()
            }
            options.add(option)
            submitsMap[option.questionId as String] = options
        }

        return questions.map { domain ->
            val vo = QuestionOrderOpenVo()
            vo.id = domain.id
            vo.title = domain.title
            vo.displayOrder = domain.displayOrder
            val submits = submitsMap[domain.id] ?: emptyList()
            val answers = optionsMap[domain.id] ?: emptyList()
            vo.accomplishStatus = calculateAccomplishStatus(submits, answers)
            return@map vo
        }
    }
}