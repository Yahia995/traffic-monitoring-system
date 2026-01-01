package com.traffic.models

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    ADMIN,
    USER
}

@Serializable
enum class VideoStatus {
    PROCESSING,
    COMPLETED,
    FAILED
}

@Serializable
enum class AIStatus {
    PENDING,
    SUCCESS,
    ERROR
}

@Serializable
enum class ViolationSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    companion object {
        fun fromString(value: String): ViolationSeverity? {
            return entries.find { it.name.equals(value, ignoreCase = true) }
        }
    }
}