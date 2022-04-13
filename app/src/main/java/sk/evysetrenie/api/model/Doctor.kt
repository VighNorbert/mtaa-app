package sk.evysetrenie.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Doctor(
    val id: Int? = null,
    var name: String,
    var surname: String,
    var title: String,
    var email: String,
    var phone: String,
    var specialisation: Specialisation,
    var appointments_length: Int,
    var address: String,
    var city: String,
    var description: String,
    var is_favourite: Boolean? = null
)