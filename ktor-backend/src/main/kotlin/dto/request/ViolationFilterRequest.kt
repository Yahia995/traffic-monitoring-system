package com.traffic.dto.request

import com.traffic.models.ViolationSeverity
import kotlinx.serialization.Serializable

@Serializable
data class ViolationFilterRequest(
    val severity: ViolationSeverity? = null,
    val plateNumber: String? = null,
    val startDate: String? = null, // ISO format
    val endDate: String? = null,
    val minSpeed: Double? = null,
    val maxSpeed: Double? = null,
    val validated: Boolean? = null,
    val page: Int = 0,
    val size: Int = 20
)