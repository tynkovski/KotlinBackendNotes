package com.tynkovski.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class NoteRequest(
    val text: String,
    val title: String = "",
    val color: Long = -1L,
)
