package com.traffic.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class HealthDetailedResponse(
    val status: String,
    val version: String,
    val timestamp: Long,
    val services: ServicesStatus
)

@Serializable
data class ServicesStatus(
    val backend: String,
    val ai_service: String,
    val database: String
)
