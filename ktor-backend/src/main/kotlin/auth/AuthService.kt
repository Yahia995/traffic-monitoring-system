package com.traffic.auth

import com.traffic.database.DatabaseFactory.dbQuery
import com.traffic.database.Users
import com.traffic.dto.RegisterRequest
import com.traffic.dto.request.LoginRequest
import com.traffic.dto.response.UserResponse
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.mindrot.jbcrypt.BCrypt
import org.slf4j.LoggerFactory
import java.time.format.DateTimeFormatter
import java.util.*

object AuthService {

    private val log = LoggerFactory.getLogger(AuthService::class.java)

    suspend fun register(req: RegisterRequest): UserResponse = dbQuery {
        log.info("Registering new user: ${req.email}")

        // Check if user exists
        val existing = Users.select { Users.email eq req.email }.singleOrNull()
        if (existing != null) {
            log.warn("Registration failed: Email ${req.email} already exists")
            throw IllegalArgumentException("Email already exists")
        }

        val userId = UUID.randomUUID()
        val hash = BCrypt.hashpw(req.password, BCrypt.gensalt())

        Users.insert {
            it[id] = userId
            it[email] = req.email
            it[passwordHash] = hash
            it[role] = req.role
        }

        log.info("User registered successfully: ${req.email} (id=$userId)")

        val user = Users.select { Users.id eq userId }.single()
        UserResponse(
            id = user[Users.id].toString(),
            email = user[Users.email],
            role = user[Users.role],
            createdAt = user[Users.createdAt].format(DateTimeFormatter.ISO_DATE_TIME)
        )
    }

    suspend fun login(req: LoginRequest): UserResponse? = dbQuery {
        log.info("Login attempt for: ${req.email}")

        val user = Users.select { Users.email eq req.email }.singleOrNull()

        if (user == null) {
            log.warn("Login failed: User ${req.email} not found")
            return@dbQuery null
        }

        if (!BCrypt.checkpw(req.password, user[Users.passwordHash])) {
            log.warn("Login failed: Invalid password for ${req.email}")
            return@dbQuery null
        }

        log.info("Login successful: ${req.email}")

        UserResponse(
            id = user[Users.id].toString(),
            email = user[Users.email],
            role = user[Users.role],
            createdAt = user[Users.createdAt].format(DateTimeFormatter.ISO_DATE_TIME)
        )
    }

    suspend fun getUserById(id: UUID): UserResponse? = dbQuery {
        Users.select { Users.id eq id }.singleOrNull()?.let {
            UserResponse(
                id = it[Users.id].toString(),
                email = it[Users.email],
                role = it[Users.role],
                createdAt = it[Users.createdAt].format(DateTimeFormatter.ISO_DATE_TIME)
            )
        }
    }
}