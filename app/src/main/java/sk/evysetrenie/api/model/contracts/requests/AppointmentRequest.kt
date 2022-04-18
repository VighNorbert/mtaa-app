package sk.evysetrenie.api.model.contracts.requests

import kotlinx.serialization.Serializable

@Serializable
data class AppointmentRequest(
    val description: String,
    var appointment_type: String
)