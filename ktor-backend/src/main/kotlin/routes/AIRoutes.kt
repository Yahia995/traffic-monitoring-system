package com.traffic.routes

import com.traffic.client.AIClient
import com.traffic.dto.ai.UploadResponseSummary
import com.traffic.plugins.userId
import com.traffic.service.VideoService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

/**
 * AI Routes - Video Upload and Processing
 *
 * V1.5 Behavior: Public endpoints (no authentication required)
 * V2.0 Behavior: Protected with JWT authentication when ENABLE_AUTHENTICATION=true
 */
fun Route.aiRoutes(aiClient: AIClient) {
    val enableAuth = System.getenv("ENABLE_AUTHENTICATION")?.toBoolean() ?: false

    // Wrap in authentication if V2 auth is enabled
    if (enableAuth) {
        authenticate("auth-jwt") {
            uploadRoutes(aiClient)
        }
    } else {
        uploadRoutes(aiClient)
    }
}

private fun Route.uploadRoutes(aiClient: AIClient) {

    post("/api/upload-video") {
        val correlationId = UUID.randomUUID().toString()
        val startTime = System.currentTimeMillis()

        call.application.log.info("[{}] ▶ Received upload request", correlationId)

        // Get userId (will be null if not authenticated, but that's OK for V1.5 compatibility)
        val userId = call.userId

        call.application.log.info("[{}] ℹ User ID: {}", correlationId, userId ?: "anonymous")

        // Receive multipart data
        val multipart = call.receiveMultipart()
        val parts = multipart.readAllParts()

        val filePart = parts.filterIsInstance<PartData.FileItem>().firstOrNull()
            ?: run {
                call.application.log.warn("[{}] ✗ No file in request", correlationId)
                throw IllegalArgumentException("No video file in request")
            }

        val bytes = filePart.streamProvider().readBytes()
        val fileName = filePart.originalFileName ?: "unknown_video.mp4"
        filePart.dispose()

        // Validate file size
        val sizeMb = bytes.size / 1024.0 / 1024.0
        val maxSizeMb = 200

        if (sizeMb > maxSizeMb) {
            call.application.log.warn(
                "[{}] ✗ File too large: {} MB (max: {} MB)",
                correlationId,
                String.format("%.2f", sizeMb),
                maxSizeMb
            )
            throw IllegalArgumentException("File too large: ${sizeMb.toInt()} MB (max: $maxSizeMb MB)")
        }

        call.application.log.info(
            "[{}] ℹ Processing video '{}' ({} MB)",
            correlationId,
            fileName,
            String.format("%.2f", sizeMb)
        )

        // Forward to AI service
        val result = aiClient.analyzeVideo(bytes, fileName, correlationId)

        // Save to database if V2 enabled
        val enableV2 = System.getenv("ENABLE_V2_PERSISTENCE")?.toBoolean() ?: false
        if (enableV2) {
            try {
                val videoId = VideoService.saveVideo(result, userId)
                call.application.log.info(
                    "[{}] 💾 Video saved to database: videoId={}{}",
                    correlationId,
                    videoId,
                    if (userId != null) " userId=$userId" else " (anonymous)"
                )
            } catch (e: Exception) {
                call.application.log.error(
                    "[{}] ❌ Failed to save to database: {}",
                    correlationId,
                    e.message,
                    e
                )
                // Don't fail the request if DB save fails
            }
        }

        val elapsed = (System.currentTimeMillis() - startTime) / 1000.0

        call.application.log.info(
            "[{}] ✓ Upload complete in {}s: violations={} vehicles={} plates={}",
            correlationId,
            String.format("%.1f", elapsed),
            result.summary.violations_detected,
            result.summary.total_vehicles_tracked,
            result.summary.vehicles_with_plates
        )

        // Return full response
        call.respond(HttpStatusCode.OK, result)
    }

    post("/api/upload-video/summary") {
        val correlationId = UUID.randomUUID().toString()
        val startTime = System.currentTimeMillis()

        call.application.log.info("[{}] ▶ Received upload request (summary mode)", correlationId)

        // Get userId
        val userId = call.userId
        call.application.log.info("[{}] ℹ User ID: {}", correlationId, userId ?: "anonymous")

        // Receive multipart data
        val multipart = call.receiveMultipart()
        val parts = multipart.readAllParts()

        val filePart = parts.filterIsInstance<PartData.FileItem>().firstOrNull()
            ?: throw IllegalArgumentException("No video file in request")

        val bytes = filePart.streamProvider().readBytes()
        val fileName = filePart.originalFileName ?: "unknown_video.mp4"
        filePart.dispose()

        val sizeMb = bytes.size / 1024.0 / 1024.0

        call.application.log.info(
            "[{}] ℹ Processing video '{}' ({} MB)",
            correlationId,
            fileName,
            String.format("%.2f", sizeMb)
        )

        // Forward to AI service
        val result = aiClient.analyzeVideo(bytes, fileName, correlationId)

        // Save to database if V2 enabled
        val enableV2 = System.getenv("ENABLE_V2_PERSISTENCE")?.toBoolean() ?: false
        if (enableV2) {
            try {
                VideoService.saveVideo(result, userId)
                call.application.log.info(
                    "[{}] 💾 Video saved to database{}",
                    correlationId,
                    if (userId != null) " (userId=$userId)" else " (anonymous)"
                )
            } catch (e: Exception) {
                call.application.log.error("[{}] ❌ Failed to save to database: {}", correlationId, e.message)
            }
        }

        val elapsed = (System.currentTimeMillis() - startTime) / 1000.0

        call.application.log.info(
            "[{}] ✓ Upload complete in {}s",
            correlationId,
            String.format("%.1f", elapsed)
        )

        // Return simplified summary
        call.respond(
            HttpStatusCode.OK,
            UploadResponseSummary(
                success = true,
                violations_count = result.summary.violations_detected,
                processing_time_seconds = elapsed,
                vehicles_tracked = result.summary.total_vehicles_tracked,
                plates_detected = result.summary.vehicles_with_plates,
                video_duration_seconds = result.video_info.duration_seconds
            )
        )
    }
}