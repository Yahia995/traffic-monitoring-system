package com.traffic.dto.response

import com.traffic.domain.models.UserRole
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: String,
    val email: String,
    val role: UserRole,
    val createdAt: String
)