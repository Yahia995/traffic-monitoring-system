package com.traffic.dto.response

import com.traffic.config.DatabaseConfig
import com.traffic.domain.entities.*
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Database plugin - initialize connection and schema
 */
fun Application.configureDatabase() {
    val enableV2 = System.getenv("ENABLE_V2_PERSISTENCE")?.toBoolean() ?: true

    if (!enableV2) {
        log.info("⚠ v2 Persistence disabled (ENABLE_V2_PERSISTENCE=false)")
        return
    }

    log.info("Configuring database...")

    // Initialize connection
    DatabaseConfig.init(environment)

    // Create schema if not exists
    try {
        transaction {
            SchemaUtils.create(
                Users,
                Videos,
                Vehicles,
                Violations,
                VehiclePositions
            )
            log.info("✓ Database schema initialized")
        }
    } catch (e: Exception) {
        log.error("✗ Failed to initialize database schema", e)
        throw e
    }

    // Register shutdown hook
    environment.monitor.subscribe(ApplicationStopping) {
        log.info("Closing database connection...")
        DatabaseConfig.close()
        log.info("✓ Database connection closed")
    }
}