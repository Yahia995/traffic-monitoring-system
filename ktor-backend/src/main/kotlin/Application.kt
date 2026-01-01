package com.traffic

import com.traffic.client.AIClient
import com.traffic.database.DatabaseFactory
import com.traffic.plugins.*
import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.netty.EngineMain
import kotlinx.coroutines.launch

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {

    val version = "2.0.0"
    log.info("=" * 60)
    log.info("🚦 Traffic Monitoring Backend v$version")
    log.info("=" * 60)

    // Configuration
    val aiEndpoint = System.getenv("KTOR_AI_ENDPOINT")
        ?: environment.config.propertyOrNull("ktor.ai.endpoint")?.getString()
        ?: "http://localhost:8000/api/process-video"

    val aiTimeout = System.getenv("KTOR_AI_TIMEOUT_MS")?.toLongOrNull()
        ?: environment.config.propertyOrNull("ktor.ai.timeout")?.getString()?.toLongOrNull()
        ?: (10 * 60 * 1000)

    log.info("📋 Configuration:")
    log.info("  AI Endpoint: $aiEndpoint")
    log.info("  AI Timeout:  ${aiTimeout / 1000}s")

    // V2 Feature Flags
    val enableV2 = System.getenv("ENABLE_V2_PERSISTENCE")?.toBoolean() ?: false
    val enableAuth = System.getenv("ENABLE_AUTHENTICATION")?.toBoolean() ?: false

    log.info("🔧 Feature Flags:")
    log.info("  V2 Persistence: ${if (enableV2) "✅ ENABLED" else "❌ DISABLED"}")
    log.info("  Authentication: ${if (enableAuth) "✅ ENABLED" else "❌ DISABLED"}")

    // Initialize Database (V2)
    if (enableV2) {
        log.info("💾 Initializing database...")
        try {
            DatabaseFactory.init()
            log.info("  ✅ Database connection established")
            log.info("  ✅ Tables created/verified")
        } catch (e: Exception) {
            log.error("  ❌ Database initialization failed: ${e.message}", e)
            throw e
        }
    } else {
        log.info("💾 Database: Skipped (V2 disabled)")
    }

    // Initialize AI Client
    val aiClient = AIClient(aiEndpoint, aiTimeout)
    log.info("🤖 AI Client initialized")

    // Register shutdown hook
    environment.monitor.subscribe(io.ktor.server.application.ApplicationStopping) {
        log.info("🛑 Application stopping - cleaning up resources...")
        try {
            aiClient.close()
            log.info("  ✅ AI client closed")
        } catch (e: Exception) {
            log.error("  ❌ Error closing AI client", e)
        }

        if (enableV2) {
            try {
                DatabaseFactory.close()
                log.info("  ✅ Database connection closed")
            } catch (e: Exception) {
                log.error("  ❌ Error closing database", e)
            }
        }
    }

    // Health Check AI Service
    if (enableV2 || enableAuth) {
        log.info("🏥 Checking AI service health...")
        kotlinx.coroutines.GlobalScope.launch {
            try {
                val isHealthy = aiClient.healthCheck()
                if (isHealthy) {
                    log.info("  ✅ AI service is healthy")
                } else {
                    log.warn("  ⚠️  AI service health check failed")
                }
            } catch (e: Exception) {
                log.warn("  ⚠️  Could not check AI service: ${e.message}")
            }
        }
    }

    // Install Plugins
    log.info("🔌 Installing plugins...")

    configureSerialization()
    log.info("  ✅ Serialization")

    configureCors()
    log.info("  ✅ CORS")

    configureStatusPages()
    log.info("  ✅ Status Pages")

    configureCallLogging()
    log.info("  ✅ Call Logging")

    if (enableAuth) {
        configureJWT()
        log.info("  ✅ JWT Authentication")
    } else {
        log.info("  ⏭️  JWT Authentication (skipped)")
    }

    configureRouting(aiClient)
    log.info("  ✅ Routing (V1.5 + V2)")

    configureSwagger()
    log.info("  ✅ Swagger Documentation")

    // Startup Complete
    log.info("=" * 60)
    log.info("✅ Backend service ready!")
    log.info("")
    log.info("📍 Endpoints:")
    log.info("  Health:    GET  /health")
    log.info("  Health:    GET  /health/detailed")
    log.info("  Upload:    POST /api/upload-video")
    log.info("  Summary:   POST /api/upload-video/summary")
    if (enableV2) {
        log.info("  Auth:      POST /api/v2/auth/register")
        log.info("  Auth:      POST /api/v2/auth/login")
        log.info("  Videos:    GET  /api/v2/videos")
        log.info("  Violations: POST /api/v2/violations/filter")
        log.info("  Stats:     GET  /api/v2/stats")
        log.info("  Export:    GET  /api/v2/violations/export/csv")
    }
    log.info("  Swagger:   GET  /swagger")
    log.info("")
    log.info("🌐 Server listening on port 8080")
    log.info("=" * 60)
}

private operator fun String.times(n: Int): String = this.repeat(n)