package com.tynkovski.data.models.responses

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String
)