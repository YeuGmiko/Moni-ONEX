package uno.moni.onex.business.task.pojo.dto

import java.io.Serializable

data class UpdateTaskConfig(
    var genCount: Int? = null,
    var modules: List<String> = listOf(),
): Serializable