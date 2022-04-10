package sk.evysetrenie.api.model

import kotlinx.serialization.Serializable

@Serializable
data class WorkSchedule(
    val weekday: Int,
    val time_from: String,
    val time_to: String
)