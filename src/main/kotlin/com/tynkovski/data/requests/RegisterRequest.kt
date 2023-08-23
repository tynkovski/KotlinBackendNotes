package com.tynkovski.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val login: String,
    val name: String,
    val password: String,
)
