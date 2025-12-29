package com.traffic.routes

import com.traffic.client.AIClient
import com.traffic.dto.ai.UploadResponseSummary
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.aiRoutes(aiClient: AIClient) {

    post("/api/upload-video") {
        val correlationId = UUID.randomUUID().toString()
        val startTime = System.currentTimeMillis()

        call.application.log.info("[{}] ▶ Received upload request", correlationId)

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

    get("/api/stats") {
        // Placeholder for future statistics endpoint
        call.respond(
            HttpStatusCode.OK,
            mapOf(
                "message" to "Statistics endpoint - coming in v2.0",
                "version" to "1.5.0"
            )
        )
    }
}