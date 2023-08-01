package com.tynkovski.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateNoteRequest(
    val text: String,
    val title: String = "",
    val color: Long = -1L,
    val tags: List<String> = listOf<String>(),
)
