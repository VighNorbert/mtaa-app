package sk.evysetrenie.api.model.contracts.responses

import kotlinx.serialization.Serializable
import sk.evysetrenie.api.model.Doctor
import sk.evysetrenie.api.model.User

@Serializable
data class AppointmentResponse(
    val id: Int,
    val time_from: String,
    val time_to: String,
    val date: String,
    val type: String,
    val description: String,
    val doctor: Doctor,
    val patient: User
)