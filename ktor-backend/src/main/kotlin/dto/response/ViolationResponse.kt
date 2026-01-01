package com.traffic.dto.response

import com.traffic.models.ViolationSeverity
import kotlinx.serialization.Serializable

@Serializable
data class ViolationResponse(
    val id: String,
    val videoId: String,
    val plateNumber: String,
    val plateConfidence: Double,
    val plateValidated: Boolean,
    val speedKmh: Double,
    val speedLimitKmh: Double,
    val overspeedKmh: Double,
    val severity: ViolationSeverity,
    val timestampSeconds: Double,
    val frameNumber: Int,
    val detectedAt: String
)