package com.traffic.dto.request

import com.traffic.domain.models.UserRole
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val role: UserRole = UserRole.USER
)