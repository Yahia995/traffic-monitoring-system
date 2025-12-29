package com.traffic.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import javax.sql.DataSource

/**
 * Database configuration and connection management
 */
object DatabaseConfig {

    private lateinit var dataSource: HikariDataSource

    /**
     * Initialize database connection from environment
     */
    fun init(environment: ApplicationEnvironment) {
        val host = System.getenv("DB_HOST") ?: "localhost"
        val port = System.getenv("DB_PORT") ?: "5432"
        val database = System.getenv("DB_NAME") ?: "traffic_monitoring"
        val user = System.getenv("DB_USER") ?: "postgres"
        val password = System.getenv("DB_PASSWORD") ?: "postgres"
        val poolSize = System.getenv("DB_POOL_SIZE")?.toIntOrNull() ?: 10

        val jdbcUrl = "jdbc:postgresql://$host:$port/$database"

        environment.log.info("Initializing database connection:")
        environment.log.info("  JDBC URL: $jdbcUrl")
        environment.log.info("  User: $user")
        environment.log.info("  Pool Size: $poolSize")

        dataSource = createDataSource(jdbcUrl, user, password, poolSize)
        Database.connect(dataSource)

        environment.log.info("✓ Database connection established")
    }

    /**
     * Create HikariCP data source
     */
    private fun createDataSource(
        jdbcUrl: String,
        user: String,
        password: String,
        poolSize: Int
    ): HikariDataSource {
        val config = HikariConfig().apply {
            this.jdbcUrl = jdbcUrl
            this.username = user
            this.password = password
            this.maximumPoolSize = poolSize
            this.minimumIdle = 2
            this.idleTimeout = 600000 // 10 minutes
            this.connectionTimeout = 30000 // 30 seconds
            this.maxLifetime = 1800000 // 30 minutes

            // PostgreSQL optimizations
            this.addDataSourceProperty("cachePrepStmts", "true")
            this.addDataSourceProperty("prepStmtCacheSize", "250")
            this.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")

            // Connection validation
            this.connectionTestQuery = "SELECT 1"
            this.validationTimeout = 5000
        }

        return HikariDataSource(config)
    }

    /**
     * Get data source for health checks
     */
    fun getDataSource(): DataSource = dataSource

    /**
     * Close database connection (for graceful shutdown)
     */
    fun close() {
        if (::dataSource.isInitialized && !dataSource.isClosed) {
            dataSource.close()
        }
    }

    /**
     * Health check - test database connection
     */
    fun isHealthy(): Boolean {
        return try {
            dataSource.connection.use { conn ->
                conn.isValid(5) // 5 second timeout
            }
        } catch (e: Exception) {
            false
        }
    }
}