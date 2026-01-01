package com.traffic.dto

import com.traffic.models.UserRole
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val role: UserRole = UserRole.USER
)