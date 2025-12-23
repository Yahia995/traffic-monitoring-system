package com.traffic.models

import kotlinx.serialization.Serializable

@Serializable
data class AIResponse(
    val violations_nbr: Int,
    val violations: Map<String, Violation>,
    val details: Map<String, VehicleDetails>
)

@Serializable data class Violation(
    val speed: Double,
    val speed_limit: Double,
    val timestamp: Double
)

@Serializable data class VehicleDetails(
    val first_frame: Int,
    val last_frame: Int,
    val positions: List<List<Int>>,
    val plate: String? = null
)