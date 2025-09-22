package uno.moni.onex.open.vo

import java.io.Serializable

data class RankRowVo(
    var order: Int? = null,
    var nickname: String? = null,
    var submits: Int? = null,
    var acCount: Int? = null,
): Serializable