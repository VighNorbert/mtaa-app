package sk.evysetrenie.api.model.contracts.responses

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val id: Int
)