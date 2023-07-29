package com.tynkovski.data.models.responses

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val token: String
)