package sk.evysetrenie.api.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int? = null,
    val name: String,
    val surname: String,
    val email: String,
    val phone: String
)