package com.traffic

import com.traffic.client.AIClient
import com.traffic.plugins.*
import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.netty.EngineMain
import kotlinx.coroutines.launch

/**
 * Enhanced Ktor Application v1.5
 *
 * Improvements:
 * - Better configuration management
 * - Enhanced logging
 * - Health check for AI service
 * - Graceful shutdown
 * - Version information
 */

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {

    // Version Information
    val version = "1.5.0"
    log.info("=" * 60)
    log.info("Traffic Monitoring Backend v$version")
    log.info("=" * 60)

    // Configuration
    val aiEndpoint = System.getenv("KTOR_AI_ENDPOINT")
        ?: environment.config.propertyOrNull("ktor.ai.endpoint")?.getString()
        ?: "http://localhost:8000/api/process-video"

    val aiTimeout = System.getenv("KTOR_AI_TIMEOUT_MS")?.toLongOrNull()
        ?: environment.config.propertyOrNull("ktor.ai.timeout")?.getString()?.toLongOrNull()
        ?: (10 * 60 * 1000) // 10 minutes default

    log.info("Configuration:")
    log.info("  AI Endpoint: $aiEndpoint")
    log.info("  AI Timeout:  ${aiTimeout / 1000}s")
    log.info("  Version:     $version")

    // Initialize AI Client
    val aiClient = AIClient(aiEndpoint, aiTimeout)

    // Register shutdown hook
    environment.monitor.subscribe(io.ktor.server.application.ApplicationStopping) {
        log.info("Application stopping - cleaning up resources...")
        try {
            aiClient.close()
            log.info("AI client closed successfully")
        } catch(e: Exception) {
            log.error("Error closing AI client", e)
        }
    }

    // Health Check AI Service
    log.info("Checking AI service health...")
    try {
        // Non-blocking health check
        kotlinx.coroutines.GlobalScope.launch {
            val isHealthy = aiClient.healthCheck()
            if(isHealthy) {
                log.info("✓ AI service is healthy")
            } else {
                log.warn("⚠ AI service health check failed - service may be unavailable")
            }
        }
    } catch(e: Exception) {
        log.warn("⚠ Could not check AI service health: ${e.message}")
    }

    // Install Plugins
    log.info("Installing plugins...")

    configureSerialization()
    log.info("  ✓ Serialization configured")

    configureCors()
    log.info("  ✓ CORS configured")

    configureStatusPages()
    log.info("  ✓ Status pages configured")

    configureCallLogging()
    log.info("  ✓ Call logging configured")

    configureRouting(aiClient)
    log.info("  ✓ Routing configured")

    configureSwagger()
    log.info("  ✓ Swagger configured")

    // Startup Complete
    log.info("=" * 60)
    log.info("✓ Backend service ready")
    log.info("  Health:  /health")
    log.info("  API:     /api/upload-video")
    log.info("  Swagger: /swagger")
    log.info("=" * 60)
}

/**
 * String repeat operator extension
 */
private operator fun String.times(n: Int): String = this.repeat(n)