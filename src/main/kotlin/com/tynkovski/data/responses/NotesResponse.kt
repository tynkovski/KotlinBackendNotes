package com.tynkovski.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class NoteResponse(
    val text: String,
    val tags: List<String>,
    val color: Long?,
)

@Serializable
data class NotesResponse(
    val notes: List<NoteResponse>
)

