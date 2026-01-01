package com.traffic.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    private lateinit var dataSource: HikariDataSource

    fun init() {
        val host = System.getenv("DB_HOST") ?: "traffic-postgres"
        val port = System.getenv("DB_PORT") ?: "5432"
        val dbName = System.getenv("DB_NAME") ?: "traffic_monitoring"
        val user = System.getenv("DB_USER") ?: "postgres"
        val password = System.getenv("DB_PASSWORD") ?: "postgres"
        val poolSize = System.getenv("DB_POOL_SIZE")?.toIntOrNull() ?: 10

        val jdbcUrl = "jdbc:postgresql://$host:$port/$dbName"

        val config = HikariConfig().apply {
            this.jdbcUrl = jdbcUrl
            username = user
            this.password = password
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = poolSize
            minimumIdle = 2
            isAutoCommit = true

            // Connection validation
            connectionTestQuery = "SELECT 1"
            validationTimeout = 5000

            // Timeouts
            connectionTimeout = 30000
            idleTimeout = 600000
            maxLifetime = 1800000

            validate()
        }

        dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        // Create tables if not exist
        transaction {
            SchemaUtils.create(
                Users,
                Videos,
                Vehicles,
                Violations,
                VehiclePositions
            )
        }
    }

    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }

    fun close() {
        if (::dataSource.isInitialized && !dataSource.isClosed) {
            dataSource.close()
        }
    }

    fun isHealthy(): Boolean {
        return try {
            if (!::dataSource.isInitialized) return false
            dataSource.connection.use { conn ->
                conn.isValid(5)
            }
        } catch (e: Exception) {
            false
        }
    }
}