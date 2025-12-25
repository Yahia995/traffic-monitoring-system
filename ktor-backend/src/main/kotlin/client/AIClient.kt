package com.traffic.client

import com.traffic.models.AIResponse
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.util.UUID
import kotlin.math.round

/**
 * Enhanced AI Client v1.5
 *
 * Features:
 * - Request correlation IDs
 * - Better error messages
 * - Detailed logging
 * - Response validation
 * - Performance metrics
 */
class AIClient(
    private val endpoint: String,
    private val timeoutMs: Long = 10 * 60 * 1000 // 10 minutes
) : Closeable {

    private val log = LoggerFactory.getLogger(AIClient::class.java)

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = false
    }

    private val client = HttpClient(Apache) {

        install(ContentNegotiation) {
            json(this@AIClient.json)
        }

        install(HttpTimeout) {
            requestTimeoutMillis = timeoutMs
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = timeoutMs
        }

        expectSuccess = false
    }

    /**
     * Determine content type from file extension
     */
    private fun contentTypeFromExtension(fileName: String): ContentType =
        when (fileName.substringAfterLast('.', "").lowercase()) {
            "mp4" -> ContentType.Video.MP4
            "avi" -> ContentType("video", "x-msvideo")
            "mov" -> ContentType.Video.QuickTime
            "mkv" -> ContentType("video", "x-matroska")
            else -> throw IllegalArgumentException("Unsupported video format: $fileName")
        }

    /**
     * Analyze video with enhanced logging and error handling
     *
     * @param fileBytes Video file content
     * @param fileName Original filename
     * @param correlationId Request correlation ID for tracing
     * @return AIResponse with analysis results
     * @throws ResponseException if AI service returns error
     */
    suspend fun analyzeVideo(
        fileBytes: ByteArray,
        fileName: String = "video.mp4",
        correlationId: String = UUID.randomUUID().toString()
    ): AIResponse {

        val startTime = System.currentTimeMillis()
        val sizeMb = fileBytes.size / 1024.0 / 1024.0

        log.info(
            "[{}] AI ▶ Sending video '{}' ({} MB) to {}",
            correlationId,
            fileName,
            String.format("%.2f", sizeMb),
            endpoint
        )

        val contentType = try {
            contentTypeFromExtension(fileName)
        } catch(e: IllegalArgumentException) {
            log.error("[{}] Invalid file format: {}", correlationId, fileName)
            throw e
        }

        val response: HttpResponse = try {
            client.post {
                url(endpoint)
                header("X-Correlation-ID", correlationId)
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append(
                                "video",
                                fileBytes,
                                Headers.build {
                                    append(HttpHeaders.ContentType, contentType.toString())
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "form-data; filename=\"$fileName\""
                                    )
                                }
                            )
                        }
                    )
                )
            }
        } catch(e: Exception) {
            val elapsed = (System.currentTimeMillis() - startTime) / 1000.0
            log.error(
                "[{}] AI ✗ Request failed after {}s: {}",
                correlationId,
                String.format("%.1f", elapsed),
                e.message
            )
            throw e
        }

        val elapsed = System.currentTimeMillis() - startTime

        // Check response status
        if(!response.status.isSuccess()) {
            val errorBody = try {
                response.bodyAsText()
            } catch(e: Exception) {
                "Unable to read error response"
            }

            log.error(
                "[{}] AI ✗ Error {} from AI service for '{}' after {}s: {}",
                correlationId,
                response.status,
                fileName,
                String.format("%.1f", elapsed),
                errorBody
            )

            throw ResponseException(
                response,
                "AI service returned ${response.status}: $errorBody"
            )
        }

        // Parse response
        val aiResponse: AIResponse = try {
            response.body()
        } catch(e: Exception) {
            log.error(
                "[{}] AI ✗ Failed to parse response for '{}': {}",
                correlationId,
                fileName,
                e.message
            )
            throw ResponseException(
                response,
                "Failed to parse AI response: ${e.message}"
            )
        }

        // Log success with metrics
        log.info(
            "[{}] AI ✓ Received response {} for '{}' in {}s",
            correlationId,
            response.status,
            fileName,
            String.format("%.1f", elapsed)
        )

        log.info(
            "[{}] AI ℹ Results: violations={} vehicles={} plates={} ai_time={}s",
            correlationId,
            aiResponse.summary.violations_detected,
            aiResponse.summary.total_vehicles_tracked,
            aiResponse.summary.vehicles_with_plates,
            String.format("%.1f", aiResponse.processing_time_seconds)
        )

        // Validate response
        validateResponse(aiResponse, correlationId)

        return aiResponse
    }

    /**
     * Validate AI response for consistency
     */
    private fun validateResponse(response: AIResponse, correlationId: String) {
        val warnings = mutableListOf<String>()

        // Check status
        if(response.status != "success") {
            warnings.add("Unexpected status: ${response.status}")
        }

        // Check violations count matches
        if(response.violations.size != response.summary.violations_detected) {
            warnings.add(
                "Violations count mismatch: list=${response.violations.size} " +
                        "summary=${response.summary.violations_detected}"
            )
        }

        // Check vehicles count matches
        if(response.tracked_vehicles.size != response.summary.total_vehicles_tracked) {
            warnings.add(
                "Vehicles count mismatch: list=${response.tracked_vehicles.size} " +
                        "summary=${response.summary.total_vehicles_tracked}"
            )
        }

        // Check for low confidence violations
        response.violations.forEach { violation ->
            if(violation.plate_confidence < 0.5) {
                warnings.add(
                    "Low confidence violation: plate=${violation.plate_number} " +
                            "confidence=${violation.plate_confidence}"
                )
            }
            if(!violation.plate_validated) {
                warnings.add(
                    "Unvalidated violation: plate=${violation.plate_number}"
                )
            }
        }

        // Log warnings
        if(warnings.isNotEmpty()) {
            log.warn("[{}] Response validation warnings:", correlationId)
            warnings.forEach { warning ->
                log.warn("[{}]   - {}", correlationId, warning)
            }
        }
    }

    /**
     * Health check for AI service
     */
    suspend fun healthCheck(): Boolean {
        return try {
            val healthEndpoint = endpoint.replace("/api/process-video", "/health")
            val response = client.get(healthEndpoint)
            val isHealthy = response.status.isSuccess()

            if(isHealthy) {
                log.debug("AI service health check: OK")
            } else {
                log.warn("AI service health check failed: {}", response.status)
            }

            isHealthy
        } catch(e: Exception) {
            log.error("AI service health check error: {}", e.message)
            false
        }
    }

    override fun close() {
        log.info("Closing AI HTTP client")
        client.close()
    }
}