package com.traffic.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val code: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val details: String? = null
)