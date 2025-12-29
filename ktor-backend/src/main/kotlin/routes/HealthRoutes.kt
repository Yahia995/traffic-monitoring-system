package com.traffic.routes

import com.traffic.client.AIClient
import com.traffic.config.DatabaseConfig
import com.traffic.dto.response.HealthDetailedResponse
import com.traffic.dto.response.HealthResponse
import com.traffic.dto.response.ServicesStatus
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.healthRoutes(aiClient: AIClient) {

    get("/health") {
        call.respond(
            HttpStatusCode.OK,
            HealthResponse(
                status = "OK",
                version = "2.0.0" // Updated version
            )
        )
    }

    get("/health/detailed") {
        val enableV2 = System.getenv("ENABLE_V2_PERSISTENCE")?.toBoolean() ?: false

        // Check AI service
        val aiHealthy = try {
            aiClient.healthCheck()
        } catch (e: Exception) {
            false
        }

        // Check database (if enabled)
        val dbHealthy = if (enableV2) {
            try {
                DatabaseConfig.isHealthy()
            } catch (e: Exception) {
                false
            }
        } else {
            null // DB not enabled
        }

        val allHealthy = aiHealthy && (dbHealthy != false)
        val status = if (allHealthy) "OK" else "DEGRADED"
        val httpStatus = if (allHealthy) HttpStatusCode.OK else HttpStatusCode.ServiceUnavailable

        call.respond(
            httpStatus,
            HealthDetailedResponse(
                status = status,
                version = "2.0.0",
                timestamp = System.currentTimeMillis(),
                services =  ServicesStatus(
                    backend = "OK",
                    ai_service = if (aiHealthy) "OK" else "UNAVAILABLE",
                    database = if (enableV2) {
                        if (dbHealthy == true) "OK" else "UNAVAILABLE"
                    } else {
                        null
                    }
                )
            )
        )
    }
}