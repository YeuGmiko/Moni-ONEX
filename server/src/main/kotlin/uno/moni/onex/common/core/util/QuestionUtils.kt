package uno.moni.onex.common.core.util

class QuestionUtils {
    companion object {
        fun getAccomplishStatus(result: List<Boolean>): Int {
            var accomplishStatus = 0
            for (item in result) {
                if (!item) {
                    accomplishStatus = 1
                    break
                }
            }
            if (accomplishStatus == 0 && result.isNotEmpty()) accomplishStatus = 2
            return accomplishStatus
        }
        fun getAccomplishStatus(answers: List<String>, submits: List<String>): Int {
            if (submits.isEmpty()) return 0
            if (answers.isEmpty()) return 2
            if (answers.size != submits.size) throw RuntimeException("not the same size og answer and submits")
            var accomplishStatus = 0
            submits.forEachIndexed { index, submit ->
                val answer = answers[index]
                if (!answer.equals(submit)) {
                    accomplishStatus = 1
                    return accomplishStatus
                }
            }
            if (answers.isNotEmpty()) accomplishStatus = 2
            return accomplishStatus
        }
    }
}