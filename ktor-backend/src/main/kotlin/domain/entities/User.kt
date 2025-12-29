package com.traffic.domain.entities

import com.traffic.domain.models.UserRole
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

/**
 * Users table - authentication and authorization
 */

object Users : UUIDTable("users") {
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role = enumerationByName("role", 20, UserRole::class).default(UserRole.USER)
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").nullable()
}