package com.traffic.domain.entities

import com.traffic.domain.models.ViolationSeverity
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Violations : UUIDTable("violations") {
    val videoId = uuid("video_id")
        .references(Videos.id, onDelete = ReferenceOption.CASCADE)

    val vehicleId = uuid("vehicle_id")
        .references(Vehicles.id, onDelete = ReferenceOption.CASCADE)

    val violationIdInVideo = varchar("violation_id_in_video", 50)

    // Violation Details
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

    init {
        index(false, videoId)
        index(false, vehicleId)
        index(false, plateNumber)
        index(false, severity)
        index(false, detectedAt)
        index(false, frameNumber)
        index(false, speedKmh)
        index(false, videoId, severity)
        index(false, detectedAt, severity)
    }
}