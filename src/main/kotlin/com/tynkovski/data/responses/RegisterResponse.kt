package com.tynkovski.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val token: String
)