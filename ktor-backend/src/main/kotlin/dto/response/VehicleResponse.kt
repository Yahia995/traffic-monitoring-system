package com.traffic.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class VehicleResponse(
    val id: String,
    val videoId: String,
    val plateNumber: String?,
    val plateConfidence: Double?,
    val plateValidated: Boolean,
    val speedKmh: Double,
    val isViolation: Boolean,
    val trackingInfo: TrackingInfoResponse
)

@Serializable
data class TrackingInfoResponse(
    val firstFrame: Int,
    val lastFrame: Int,
    val framesTracked: Int,
    val trajectoryLength: Double
)