package uno.moni.onex.business.rank.pojo.domain

import java.time.LocalDateTime

class DailySubmit(
    var id: String? = null,
    var userId: String? = null,
    var questionId: String? = null,
    var result: Int? = null,
    var submitTime: LocalDateTime? = null,
)