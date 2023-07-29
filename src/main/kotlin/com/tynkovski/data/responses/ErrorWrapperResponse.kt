package com.tynkovski.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class ErrorWrapperResponse(
    val message: String
)