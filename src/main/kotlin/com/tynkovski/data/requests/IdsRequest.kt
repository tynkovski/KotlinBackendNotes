package com.tynkovski.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class IdsRequest(
    val ids: List<String>
)
