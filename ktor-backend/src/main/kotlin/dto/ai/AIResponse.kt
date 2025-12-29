package com.traffic.dto.ai

import kotlinx.serialization.Serializable

@Serializable
data class UploadResponseSummary(
    val success: Boolean,
    val violations_count: Int,
    val processing_time_seconds: Double,
    val vehicles_tracked: Int,
    val plates_detected: Int,
    val video_duration_seconds: Double
)

@Serializable
data class AIResponse(
    val status: String,
    val processing_time_seconds: Double,
    val video_info: VideoInfo,
    val summary: Summary,
    val violations: List<Violation>,
    val tracked_vehicles: List<TrackedVehicle>,
    val configuration: Configuration
)

@Serializable
data class VideoInfo(
    val filename: String,
    val duration_seconds: Double,
    val fps: Double,
    val total_frames: Int,
    val processed_frames: Int
)

@Serializable
data class Summary(
    val total_vehicles_tracked: Int,
    val vehicles_with_plates: Int,
    val violations_detected: Int,
    val average_speed_kmh: Double
)

@Serializable
data class Violation(
    val violation_id: String,
    val plate_number: String,
    val plate_confidence: Double,
    val plate_validated: Boolean,
    val speed_kmh: Double,
    val speed_limit_kmh: Double,
    val overspeed_kmh: Double,
    val timestamp_seconds: Double,
    val frame_number: Int,
    val severity: String
)

@Serializable
data class TrackedVehicle(
    val vehicle_id: String,
    val tracking_info: TrackingInfo,
    val plate_info: PlateInfo,
    val speed_info: SpeedInfo,
    val positions: List<Position>
)

@Serializable
data class TrackingInfo(
    val first_frame: Int,
    val last_frame: Int,
    val frames_tracked: Int,
    val trajectory_length_pixels: Double
)

@Serializable
data class PlateInfo(
    val plate_number: String?,
    val raw_ocr_text: String,
    val confidence: Double,
    val validated: Boolean,
    val corrections_applied: List<String>? = null,
    val validation_errors: List<String>? = null,
    val detection_frame: Int? = null
)

@Serializable
data class SpeedInfo(
    val speed_kmh: Double,
    val is_violation: Boolean,
    val calculation_valid: Boolean
)

@Serializable
data class Position(
    val frame: Int,
    val x: Int,
    val y: Int
)

@Serializable
data class Configuration(
    val speed_limit_kmh: Double,
    val pixel_to_meter: Double,
    val min_tracked_frames: Int,
    val frame_skip: Int,
    val vehicle_confidence: Double,
    val plate_confidence: Double,
    val ocr_confidence: Double
)