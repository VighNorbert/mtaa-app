package sk.evysetrenie.api.model.contracts.responses

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    val code: Int,
    val message: String = "Vyskytla sa chyba"
)