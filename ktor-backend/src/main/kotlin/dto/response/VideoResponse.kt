package com.traffic.dto.response

import com.traffic.domain.models.AIStatus
import com.traffic.domain.models.VideoStatus
import kotlinx.serialization.Serializable

@Serializable
data class VideoResponse(
    val id: String,
    val filename: String,
    val uploadedAt: String,
    val durationSeconds: Double,
    val fps: Double,
    val totalFrames: Int,
    val processedFrames: Int,
    val processingTimeSeconds: Double,
    val status: VideoStatus,
    val aiStatus: AIStatus,
    val summary: VideoSummary
)

@Serializable
data class VideoSummary(
    val totalVehicles: Int,
    val vehiclesWithPlates: Int,
    val violations: Int,
    val averageSpeed: Double
)