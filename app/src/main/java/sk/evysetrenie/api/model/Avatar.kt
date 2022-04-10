package sk.evysetrenie.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Avatar(
    val file: String? = null,
    val filename: String,
    val extension: String
)