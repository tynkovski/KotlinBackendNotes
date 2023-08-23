package com.tynkovski.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class EditNameRequest(
    val name: String,
)