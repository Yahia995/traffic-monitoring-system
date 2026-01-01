package com.traffic.plugins

import com.traffic.client.AIClient
import com.traffic.routes.aiRoutes
import com.traffic.routes.healthRoutes
import com.traffic.routes.v2Routes
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

/**
 * Enhanced routing configuration v2.0
 *
 * Features:
 * - Health check routes (basic + detailed)
 * - Video upload routes (full + summary) - V1.5
 * - V2 routes (auth, videos, violations, stats) - V2.0
 * - Root information endpoint
 */
fun Application.configureRouting(aiClient: AIClient) {
    routing {

        // Root endpoint with API information
        get("/") {
            val enableV2 = System.getenv("ENABLE_V2_PERSISTENCE")?.toBoolean() ?: false

            val v2Info = if (enableV2) {
                """
                
                V2 Endpoints:
                - POST /api/v2/auth/register      User registration
                - POST /api/v2/auth/login         User login
                - GET  /api/v2/videos             List videos (paginated)
                - GET  /api/v2/videos/{id}        Get video details
                - DELETE /api/v2/videos/{id}      Delete video
                - POST /api/v2/violations/filter  Filter violations
                - GET  /api/v2/violations/{id}    Get violation details
                - GET  /api/v2/violations/export/csv Export violations CSV
                - GET  /api/v2/stats              Get statistics
                """.trimIndent()
            } else {
                ""
            }

            call.respondText(
                """
                🚦 Traffic Monitoring Backend API v2.0
                
                V1.5 Endpoints:
                - GET  /health                    Health check
                - GET  /health/detailed           Detailed health with AI & DB status
                - POST /api/upload-video          Upload video for analysis (full response)
                - POST /api/upload-video/summary  Upload video for analysis (summary only)
                - GET  /swagger                   API documentation
                $v2Info
                
                Status: Operational
                Version: 2.0.0
                V2 Features: ${if (enableV2) "ENABLED ✅" else "DISABLED ❌"}
                """.trimIndent()
            )
        }

        // Health routes
        healthRoutes(aiClient)

        // AI processing routes (V1.5)
        aiRoutes(aiClient)

        // V2 routes
        v2Routes()
    }
}