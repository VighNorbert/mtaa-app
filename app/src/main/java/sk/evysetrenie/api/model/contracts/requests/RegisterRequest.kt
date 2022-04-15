package sk.evysetrenie.api.model.contracts.requests

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val name: String,
    val surname: String,
    val email: String,
    val phone: String,
    var password: String
)