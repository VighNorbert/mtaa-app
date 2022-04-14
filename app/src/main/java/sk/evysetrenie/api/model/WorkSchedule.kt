package sk.evysetrenie.api.model

import kotlinx.serialization.Serializable

@Serializable
data class WorkSchedule(
    var weekday: Int,
    var time_from: String,
    var time_to: String
)