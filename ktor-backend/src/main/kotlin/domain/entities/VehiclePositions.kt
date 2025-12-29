package com.traffic.domain.entities

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption

/**
 * VehiclePositions table - trajectory tracking points
 * (Optional - only if INCLUDE_TRAJECTORY=true)
 */
object VehiclePositions : UUIDTable("vehicle_positions") {
    val vehicleId = uuid("vehicle_id")
        .references(Vehicles.id, onDelete = ReferenceOption.CASCADE)

    val frame = integer("frame")
    val x = integer("x")
    val y = integer("y")

    init {
        index(false, vehicleId, frame)
    }
}