package com.tynkovski.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateNoteRequest(
    val text: String,
    val title: String? = null,
    val color: Long? = null,
    val tags: List<String> = listOf<String>(),
)
