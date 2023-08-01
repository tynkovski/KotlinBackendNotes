package com.tynkovski.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class NoteCreated(
    val id: String
)