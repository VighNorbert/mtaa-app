package sk.evysetrenie.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Specialisation(
    val id: Int,
    val title: String
)