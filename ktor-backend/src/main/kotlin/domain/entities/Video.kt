package com.traffic.domain.entities

import com.traffic.domain.models.AIStatus
import com.traffic.domain.models.VideoStatus
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

/**
 * Videos table - uploaded traffic videos and processing metadata
 */

object Videos : UUIDTable("videos") {
    val uploadedBy = uuid("uploaded_by")
        .references(Users.id, onDelete = ReferenceOption.SET_NULL)
        .nullable()

    val filename = varchar("filename", 255)
    val uploadedAt = datetime("uploaded_at").clientDefault { LocalDateTime.now() }

    // Video Metadata
    val durationSeconds = double("duration_seconds")
    val fps = double("fps")
    val totalFrames = integer("total_frames")
    val processedFrames = integer("processed_frames")

    // Processing Metadata
    val processingTimeSeconds = double("processing_time_seconds")
    val status = enumerationByName("status", 50, VideoStatus::class).default(VideoStatus.COMPLETED)
    val aiStatus = enumerationByName("ai_status", 20, AIStatus::class).default(AIStatus.SUCCESS)

    // Configuration Snapshot
    val speedLimitKmh = double("speed_limit_kmh")
    val pixelToMeter = double("pixel_to_meter")
    val frameSkip = integer("frame_skip")
    val minTrackedFrames = integer("min_tracked_frames")
    val vehicleConfidence = double("vehicle_confidence")
    val plateConfidence = double("plate_confidence")
    val ocrConfidence = double("ocr_confidence")

    init {
        index(false, uploadedBy)
        index(false, uploadedAt)
        index(false, status)
        index(false, aiStatus)
    }
}