package com.traffic.service

import com.traffic.database.*
import com.traffic.database.DatabaseFactory.dbQuery
import com.traffic.models.ViolationSeverity
import com.traffic.dto.ai.AIResponse
import com.traffic.dto.response.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.slf4j.LoggerFactory
import java.time.format.DateTimeFormatter
import java.util.*

object VideoService {

    private val log = LoggerFactory.getLogger(VideoService::class.java)

    suspend fun saveVideo(aiResponse: AIResponse, userId: UUID?): UUID = dbQuery {
        val videoId = UUID.randomUUID()

        log.info("Saving video analysis to database: ${aiResponse.video_info.filename} (videoId=$videoId)")

        try {
            // Insert video
            Videos.insert {
                it[id] = videoId
                it[uploadedBy] = userId
                it[filename] = aiResponse.video_info.filename
                it[durationSeconds] = aiResponse.video_info.duration_seconds
                it[fps] = aiResponse.video_info.fps
                it[totalFrames] = aiResponse.video_info.total_frames
                it[processedFrames] = aiResponse.video_info.processed_frames
                it[processingTimeSeconds] = aiResponse.processing_time_seconds
                it[speedLimitKmh] = aiResponse.configuration.speed_limit_kmh
                it[pixelToMeter] = aiResponse.configuration.pixel_to_meter
            }
            log.info("  ✅ Video record created")

            // Save vehicles
            var vehiclesSaved = 0
            var positionsSaved = 0
            val vehicleIdMap = mutableMapOf<String, UUID>()

            aiResponse.tracked_vehicles.forEach { vehicle ->
                val vehicleDbId = UUID.randomUUID()
                vehicleIdMap[vehicle.vehicle_id] = vehicleDbId

                Vehicles.insert {
                    it[id] = vehicleDbId
                    it[this.videoId] = videoId
                    it[vehicleIdInVideo] = vehicle.vehicle_id
                    it[plateNumber] = vehicle.plate_info.plate_number
                    it[rawOcrText] = vehicle.plate_info.raw_ocr_text
                    it[plateConfidence] = vehicle.plate_info.confidence
                    it[plateValidated] = vehicle.plate_info.validated
                    it[correctionsApplied] = vehicle.plate_info.corrections_applied?.joinToString(",")
                    it[detectionFrame] = vehicle.plate_info.detection_frame
                    it[firstFrame] = vehicle.tracking_info.first_frame
                    it[lastFrame] = vehicle.tracking_info.last_frame
                    it[framesTracked] = vehicle.tracking_info.frames_tracked
                    it[trajectoryLengthPixels] = vehicle.tracking_info.trajectory_length_pixels
                    it[speedKmh] = vehicle.speed_info.speed_kmh
                    it[isViolation] = vehicle.speed_info.is_violation
                }
                vehiclesSaved++

                // Save positions
                vehicle.positions.forEach { pos ->
                    VehiclePositions.insert {
                        it[this.vehicleId] = vehicleDbId
                        it[frame] = pos.frame
                        it[x] = pos.x
                        it[y] = pos.y
                    }
                    positionsSaved++
                }
            }
            log.info("  ✅ Saved $vehiclesSaved vehicles with $positionsSaved trajectory points")

            // Save violations
            var violationsSaved = 0
            aiResponse.violations.forEach { violation ->
                val vehicleDbId = vehicleIdMap[violation.plate_number]
                    ?: Vehicles.select {
                        (Vehicles.videoId eq videoId) and
                                (Vehicles.plateNumber eq violation.plate_number)
                    }.firstOrNull()?.get(Vehicles.id)?.value

                if (vehicleDbId != null) {
                    Violations.insert {
                        it[id] = UUID.randomUUID()
                        it[this.videoId] = videoId
                        it[this.vehicleId] = vehicleDbId
                        it[violationIdInVideo] = violation.violation_id
                        it[plateNumber] = violation.plate_number
                        it[plateConfidence] = violation.plate_confidence
                        it[plateValidated] = violation.plate_validated
                        it[speedKmh] = violation.speed_kmh
                        it[speedLimitKmh] = violation.speed_limit_kmh
                        it[overspeedKmh] = violation.overspeed_kmh
                        it[timestampSeconds] = violation.timestamp_seconds
                        it[frameNumber] = violation.frame_number
                        it[severity] = ViolationSeverity.valueOf(violation.severity.uppercase())
                    }
                    violationsSaved++
                } else {
                    log.warn("  ⚠️  Could not find vehicle for violation plate: ${violation.plate_number}")
                }
            }
            log.info("  ✅ Saved $violationsSaved violations")

            log.info("✅ Video analysis saved successfully: videoId=$videoId")
            videoId

        } catch (e: Exception) {
            log.error("❌ Failed to save video analysis: ${e.message}", e)
            throw e
        }
    }

    suspend fun listVideos(userId: UUID?, page: Int, size: Int): PaginatedResponse<VideoResponse> = dbQuery {
        log.info("Fetching videos: userId=${userId ?: "all"}, page=$page, size=$size")

        val query = if (userId != null) {
            Videos.select { Videos.uploadedBy eq userId }
        } else {
            Videos.selectAll()
        }

        val total = query.count()
        val videos = query
            .orderBy(Videos.uploadedAt, SortOrder.DESC)
            .limit(size, offset = (page * size).toLong())
            .map { toVideoResponse(it) }

        log.info("Found ${videos.size} videos (total: $total)")

        PaginatedResponse(
            data = videos,
            page = page,
            size = size,
            totalElements = total,
            totalPages = ((total + size - 1) / size).toInt()
        )
    }

    suspend fun getVideo(videoId: UUID): VideoResponse? = dbQuery {
        log.info("Fetching video: $videoId")
        Videos.select { Videos.id eq videoId }
            .singleOrNull()
            ?.let { toVideoResponse(it) }
    }

    suspend fun deleteVideo(videoId: UUID): Boolean = dbQuery {
        log.info("Deleting video: $videoId")
        val deleted = Videos.deleteWhere { id eq videoId } > 0
        if (deleted) {
            log.info("  ✅ Video deleted: $videoId")
        } else {
            log.warn("  ⚠️  Video not found: $videoId")
        }
        deleted
    }

    private fun toVideoResponse(row: ResultRow): VideoResponse {
        val videoId = row[Videos.id].value

        val vehiclesCount = Vehicles.select { Vehicles.videoId eq videoId }.count().toInt()
        val platesCount = Vehicles.select {
            (Vehicles.videoId eq videoId) and (Vehicles.plateNumber.isNotNull())
        }.count().toInt()
        val violationsCount = Violations.select { Violations.videoId eq videoId }.count().toInt()

        val avgSpeed = Vehicles.select { Vehicles.videoId eq videoId }
            .map { it[Vehicles.speedKmh] }
            .average()

        return VideoResponse(
            id = videoId.toString(),
            filename = row[Videos.filename],
            uploadedAt = row[Videos.uploadedAt].format(DateTimeFormatter.ISO_DATE_TIME),
            durationSeconds = row[Videos.durationSeconds],
            fps = row[Videos.fps],
            totalFrames = row[Videos.totalFrames],
            processedFrames = row[Videos.processedFrames],
            processingTimeSeconds = row[Videos.processingTimeSeconds],
            status = row[Videos.status],
            aiStatus = row[Videos.aiStatus],
            summary = VideoSummary(
                totalVehicles = vehiclesCount,
                vehiclesWithPlates = platesCount,
                violations = violationsCount,
                averageSpeed = avgSpeed
            )
        )
    }
}