package com.traffic.routes

import com.traffic.client.AIClient
import com.traffic.models.HealthResponse
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
                version = "1.5.0"
            )
        )
    }

    get("/health/detailed") {
        val aiHealthy = try {
            aiClient.healthCheck()
        } catch (e: Exception) {
            false
        }

        val status = if (aiHealthy) "OK" else "DEGRADED"
        val httpStatus = if (aiHealthy) HttpStatusCode.OK else HttpStatusCode.ServiceUnavailable

        call.respond(
            httpStatus,
            mapOf(
                "status" to status,
                "version" to "1.5.0",
                "timestamp" to System.currentTimeMillis(),
                "services" to mapOf(
                    "backend" to "OK",
                    "ai_service" to if (aiHealthy) "OK" else "UNAVAILABLE"
                )
            )
        )
    }
}