package com.traffic.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(
    val status: String,
    val version: String = "1.5.0",
    val timestamp: Long = System.currentTimeMillis()
)