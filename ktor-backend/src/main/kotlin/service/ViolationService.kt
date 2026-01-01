package com.traffic.service

import com.traffic.database.DatabaseFactory.dbQuery
import com.traffic.database.Violations
import com.traffic.dto.request.ViolationFilterRequest
import com.traffic.dto.response.PaginatedResponse
import com.traffic.dto.response.ViolationResponse
import com.traffic.dto.response.ViolationStatsResponse
import org.jetbrains.exposed.sql.*
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object ViolationService {

    private val log = LoggerFactory.getLogger(ViolationService::class.java)

    suspend fun listViolations(filter: ViolationFilterRequest): PaginatedResponse<ViolationResponse> = dbQuery {
        log.info("Filtering violations: severity=${filter.severity}, plate=${filter.plateNumber}, page=${filter.page}")

        var query = Violations.selectAll()

        filter.severity?.let {
            query = query.andWhere { Violations.severity eq it }
            log.debug("  Filter: severity=$it")
        }
        filter.plateNumber?.let {
            query = query.andWhere { Violations.plateNumber like "%$it%" }
            log.debug("  Filter: plate contains '$it'")
        }
        filter.startDate?.let {
            val date = LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME)
            query = query.andWhere { Violations.detectedAt greaterEq date }
            log.debug("  Filter: startDate=$it")
        }
        filter.endDate?.let {
            val date = LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME)
            query = query.andWhere { Violations.detectedAt lessEq date }
            log.debug("  Filter: endDate=$it")
        }
        filter.minSpeed?.let {
            query = query.andWhere { Violations.speedKmh greaterEq it }
            log.debug("  Filter: minSpeed=$it")
        }
        filter.maxSpeed?.let {
            query = query.andWhere { Violations.speedKmh lessEq it }
            log.debug("  Filter: maxSpeed=$it")
        }
        filter.validated?.let {
            query = query.andWhere { Violations.plateValidated eq it }
            log.debug("  Filter: validated=$it")
        }

        val total = query.count()
        val violations = query
            .orderBy(Violations.detectedAt, SortOrder.DESC)
            .limit(filter.size, offset = (filter.page * filter.size).toLong())
            .map { toViolationResponse(it) }

        log.info("Found ${violations.size} violations (total: $total)")

        PaginatedResponse(
            data = violations,
            page = filter.page,
            size = filter.size,
            totalElements = total,
            totalPages = ((total + filter.size - 1) / filter.size).toInt()
        )
    }

    suspend fun getViolation(id: UUID): ViolationResponse? = dbQuery {
        log.info("Fetching violation: $id")
        Violations.select { Violations.id eq id }
            .singleOrNull()
            ?.let { toViolationResponse(it) }
    }

    suspend fun getStats(): ViolationStatsResponse = dbQuery {
        log.info("Calculating violation statistics")

        val total = Violations.selectAll().count()

        val bySeverity = Violations.slice(Violations.severity, Violations.id.count())
            .selectAll()
            .groupBy(Violations.severity)
            .associate { it[Violations.severity].name to it[Violations.id.count()].toInt() }

        val avgSpeed = Violations.slice(Violations.speedKmh)
            .selectAll()
            .map { it[Violations.speedKmh] }
            .average()
            .takeIf { !it.isNaN() } ?: 0.0

        log.info("Stats: total=$total, avgSpeed=$avgSpeed, bySeverity=$bySeverity")

        ViolationStatsResponse(
            total = total,
            bySeverity = bySeverity,
            averageSpeed = avgSpeed
        )
    }

    private fun toViolationResponse(row: ResultRow) = ViolationResponse(
        id = row[Violations.id].toString(),
        videoId = row[Violations.videoId].toString(),
        plateNumber = row[Violations.plateNumber],
        plateConfidence = row[Violations.plateConfidence],
        plateValidated = row[Violations.plateValidated],
        speedKmh = row[Violations.speedKmh],
        speedLimitKmh = row[Violations.speedLimitKmh],
        overspeedKmh = row[Violations.overspeedKmh],
        severity = row[Violations.severity],
        timestampSeconds = row[Violations.timestampSeconds],
        frameNumber = row[Violations.frameNumber],
        detectedAt = row[Violations.detectedAt].format(DateTimeFormatter.ISO_DATE_TIME)
    )
}