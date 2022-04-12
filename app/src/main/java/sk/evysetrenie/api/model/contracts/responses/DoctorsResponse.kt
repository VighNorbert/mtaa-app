package sk.evysetrenie.api.model.contracts.responses

import kotlinx.serialization.Serializable
import sk.evysetrenie.api.model.Doctor
import sk.evysetrenie.api.model.Specialisation
import sk.evysetrenie.api.model.User

@Serializable
data class DoctorsResponse(
    val id: Int,
    val name: String,
    val surname: String,
    val title: String,
    val email: String,
    val phone: String,
    val specialisation: Specialisation,
    val appointments_length: Int,
    val address: String,
    val city: String,
    val description: String,
    var is_favourite: Boolean
)