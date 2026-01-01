package com.traffic.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(
    val status: String,
    val version: String,
    val timestamp: Long = System.currentTimeMillis()
)