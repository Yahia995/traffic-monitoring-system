package com.traffic.plugins

import com.traffic.client.AIClient
import com.traffic.routes.aiRoutes
import com.traffic.routes.healthRoutes
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

/**
 * Enhanced routing configuration v1.5
 *
 * Features:
 * - Health check routes (basic + detailed)
 * - Video upload routes (full + summary)
 * - Root information endpoint
 * - Future statistics endpoint
 */
fun Application.configureRouting(aiClient: AIClient) {
    routing {

        // Root endpoint with API information
        get("/") {
            call.respondText(
                """
                🚦 Traffic Monitoring Backend API v1.5
                
                Available Endpoints:
                - GET  /health                    Health check
                - GET  /health/detailed           Detailed health with AI service status
                - POST /api/upload-video          Upload video for analysis (full response)
                - POST /api/upload-video/summary  Upload video for analysis (summary only)
                - GET  /api/stats                 Statistics (coming in v2.0)
                - GET  /swagger                   API documentation
                
                Status: Operational
                Version: 1.5.0
                """.trimIndent()
            )
        }

        // Health routes
        healthRoutes(aiClient)

        // AI processing routes
        aiRoutes(aiClient)
    }
}