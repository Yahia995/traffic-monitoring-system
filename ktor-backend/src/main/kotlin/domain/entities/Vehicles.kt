package com.traffic.domain.entities

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption

/**
 * Vehicles table - tracked vehicles from video analysis
 */
object Vehicles : UUIDTable("vehicles") {
    val videoId = uuid("video_id")
        .references(Videos.id, onDelete = ReferenceOption.CASCADE)

    val vehicleIdInVideo = varchar("vehicle_id_in_video", 50) // e.g., "veh_001"

    // Plate Information (nullable if no plate detected)
    val plateNumber = varchar("plate_number", 50).nullable()
    val rawOcrText = varchar("raw_ocr_text", 100).nullable()
    val plateConfidence = double("plate_confidence").nullable()
    val plateValidated = bool("plate_validated").default(false)

    // JSON fields stored as TEXT
    val correctionsApplied = text("corrections_applied").nullable()
    val validationErrors = text("validation_errors").nullable()

    val detectionFrame = integer("detection_frame").nullable()

    // Tracking Information
    val firstFrame = integer("first_frame")
    val lastFrame = integer("last_frame")
    val framesTracked = integer("frames_tracked")
    val trajectoryLengthPixels = double("trajectory_length_pixels")

    // Speed Information
    val speedKmh = double("speed_kmh")
    val isViolation = bool("is_violation")
    val calculationValid = bool("calculation_valid")

    init {
        index(false, videoId)
        index(false, plateNumber)
        index(false, isViolation)
        index(false, videoId, vehicleIdInVideo)
    }
}