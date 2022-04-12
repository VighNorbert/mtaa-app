package sk.evysetrenie.api.model.contracts.requests

import kotlinx.serialization.Serializable

@Serializable
data class DoctorsRequest(
    val name: String?,
    val specialisation: Int?,
    val city: String?,
    val only_favourites: Boolean,
    val page: Int,
    val per_page: Int
)