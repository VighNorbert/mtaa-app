package sk.evysetrenie.api.model.contracts.responses

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: ApiError
)