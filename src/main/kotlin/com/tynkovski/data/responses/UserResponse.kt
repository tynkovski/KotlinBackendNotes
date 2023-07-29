package com.tynkovski.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse (
    val id: String,
    val login: String
)