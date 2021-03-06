package sk.evysetrenie.api.model.contracts.requests

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    var password: String
)