package com.tynkovski.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class PasswordRequest(
    val oldPassword: String,
    val newPassword: String,
)