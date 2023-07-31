package com.tynkovski.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class NoteResponse(
    val text: String,
    val title: String?,
    val tags: List<String>,
    val color: Long?,
    val createdAt: Long,
)

@Serializable
data class NotesResponse(
    val notes: List<NoteResponse>
)

