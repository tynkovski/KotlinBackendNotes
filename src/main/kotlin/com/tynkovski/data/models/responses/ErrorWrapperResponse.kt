package com.tynkovski.data.models.responses

import kotlinx.serialization.Serializable

@Serializable
data class ErrorWrapperResponse(
    val message: String
)