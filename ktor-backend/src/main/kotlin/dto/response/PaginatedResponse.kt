package com.traffic.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResponse<T>(
    val data: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int
)