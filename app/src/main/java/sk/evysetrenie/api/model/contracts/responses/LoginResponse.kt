package sk.evysetrenie.api.model.contracts.responses

import kotlinx.serialization.Serializable
import sk.evysetrenie.api.model.Doctor
import sk.evysetrenie.api.model.User

@Serializable
data class LoginResponse(
    val user: User? = null,
    val doctor: Doctor? = null,
    val access_token: String
)