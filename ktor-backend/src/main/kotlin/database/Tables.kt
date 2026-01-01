package com.traffic.database

import com.traffic.models.*
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Users : UUIDTable("users") {
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role = enumerationByName("role", 20, UserRole::class).default(UserRole.USER)
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
}

object Videos : UUIDTable("videos") {
    val uploadedBy = uuid("uploaded_by").references(Users.id, onDelete = ReferenceOption.SET_NULL).nullable()
    val filename = varchar("filename", 255)
    val uploadedAt = datetime("uploaded_at").clientDefault { LocalDateTime.now() }
    val durationSeconds = double("duration_seconds")
    val fps = double("fps")
    val totalFrames = integer("total_frames")
    val processedFrames = integer("processed_frames")
    val processingTimeSeconds = double("processing_time_seconds")
    val status = enumerationByName("status", 50, VideoStatus::class).default(VideoStatus.COMPLETED)
    val aiStatus = enumerationByName("ai_status", 20, AIStatus::class).default(AIStatus.SUCCESS)
    val speedLimitKmh = double("speed_limit_kmh")
    val pixelToMeter = double("pixel_to_meter")
}

object Vehicles : UUIDTable("vehicles") {
    val videoId = uuid("video_id").references(Videos.id, onDelete = ReferenceOption.CASCADE)
    val vehicleIdInVideo = varchar("vehicle_id_in_video", 50)
    val plateNumber = varchar("plate_number", 50).nullable()
    val rawOcrText = varchar("raw_ocr_text", 100).nullable()
    val plateConfidence = double("plate_confidence").nullable()
    val plateValidated = bool("plate_validated").default(false)
    val correctionsApplied = text("corrections_applied").nullable()
    val detectionFrame = integer("detection_frame").nullable()
    val firstFrame = integer("first_frame")
    val lastFrame = integer("last_frame")
    val framesTracked = integer("frames_tracked")
    val trajectoryLengthPixels = double("trajectory_length_pixels")
    val speedKmh = double("speed_kmh")
    val isViolation = bool("is_violation")
}

object Violations : UUIDTable("violations") {
    val videoId = uuid("video_id").references(Videos.id, onDelete = ReferenceOption.CASCADE)
    val vehicleId = uuid("vehicle_id").references(Vehicles.id, onDelete = ReferenceOption.CASCADE)
    val violationIdInVideo = varchar("violation_id_in_video", 50)
    val plateNumber = varchar("plate_number", 50)
    val plateConfidence = double("plate_confidence")
    val plateValidated = bool("plate_validated")
    val speedKmh = double("speed_kmh")
    val speedLimitKmh = double("speed_limit_kmh")
    val overspeedKmh = double("overspeed_kmh")
    val timestampSeconds = double("timestamp_seconds")
    val frameNumber = integer("frame_number")
    val severity = enumerationByName("severity", 20, ViolationSeverity::class)
    val detectedAt = datetime("detected_at").clientDefault { LocalDateTime.now() }
}

object VehiclePositions : UUIDTable("vehicle_positions") {
    val vehicleId = uuid("vehicle_id").references(Vehicles.id, onDelete = ReferenceOption.CASCADE)
    val frame = integer("frame")
    val x = integer("x")
    val y = integer("y")
}