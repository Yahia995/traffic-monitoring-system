package com.traffic.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ViolationStatsResponse(
    val total: Long,
    val averageSpeed: Double,
    val bySeverity: Map<String, Int>,
)