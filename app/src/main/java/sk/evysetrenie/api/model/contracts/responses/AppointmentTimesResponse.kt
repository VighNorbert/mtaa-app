package sk.evysetrenie.api.model.contracts.responses

import kotlinx.serialization.Serializable
import sk.evysetrenie.api.model.Doctor
import sk.evysetrenie.api.model.User

@Serializable
data class AppointmentTimesResponse(
    val id: Int,
    val time_from: String,
    val time_to: String,
    val date: String,
)