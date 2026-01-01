package com.traffic.routes

import com.traffic.client.AIClient
import com.traffic.database.DatabaseFactory
import com.traffic.dto.response.HealthDetailedResponse
import com.traffic.dto.response.HealthResponse
import com.traffic.dto.response.ServicesStatus
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.application.log
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.healthRoutes(aiClient: AIClient) {

    get("/health") {
        call.respond(
            HttpStatusCode.OK,
            HealthResponse(
                status = "OK",
                version = "2.0.0"
            )
        )
    }

    get("/health/detailed") {
        val enableV2 = System.getenv("ENABLE_V2_PERSISTENCE")?.toBoolean() ?: false

        // Check AI service
        val aiHealthy = try {
            aiClient.healthCheck()
        } catch (e: Exception) {
            call.application.log.warn("AI health check failed: ${e.message}")
            false
        }

        // Check database (if enabled)
        val dbHealthy = if (enableV2) {
            try {
                DatabaseFactory.isHealthy()
            } catch (e: Exception) {
                call.application.log.warn("Database health check failed: ${e.message}")
                false
            }
        } else {
            null // DB not enabled
        }

        val allHealthy = aiHealthy && (dbHealthy != false)
        val status = if (allHealthy) "OK" else "DEGRADED"
        val httpStatus = if (allHealthy) HttpStatusCode.OK else HttpStatusCode.ServiceUnavailable

        call.application.log.info(
            "Health check: AI={} DB={} Status={}",
            if (aiHealthy) "OK" else "FAIL",
            when (dbHealthy) {
                true -> "OK"
                false -> "FAIL"
                null -> "DISABLED"
            },
            status
        )

        call.respond(
            httpStatus,
            HealthDetailedResponse(
                status = status,
                version = "2.0.0",
                timestamp = System.currentTimeMillis(),
                services = ServicesStatus(
                    backend = "OK",
                    ai_service = if (aiHealthy) "OK" else "UNAVAILABLE",
                    database = when (dbHealthy) {
                        true -> "OK"
                        false -> "UNAVAILABLE"
                        null -> "DISABLED"
                    }
                )
            )
        )
    }
}