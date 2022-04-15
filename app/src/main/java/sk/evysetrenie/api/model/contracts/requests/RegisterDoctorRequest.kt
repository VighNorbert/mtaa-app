package sk.evysetrenie.api.model.contracts.requests

import kotlinx.serialization.Serializable
import sk.evysetrenie.api.model.Avatar
import sk.evysetrenie.api.model.WorkSchedule

@Serializable
data class RegisterDoctorRequest(
    val name: String,
    val surname: String,
    val title: String,
    val email: String,
    val phone: String,
    val password: String?,
    val specialisation_id: Int,
    val appointments_length: Int,
    val address: String,
    val city: String,
    val description: String,
    val schedules: List<WorkSchedule>,
    val avatar: Avatar? = null
)