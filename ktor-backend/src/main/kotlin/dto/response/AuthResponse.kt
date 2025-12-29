package com.traffic.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val expiresAt: Long,
    val user: UserResponse
)