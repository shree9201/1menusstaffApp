package com.droptechsolution.shared.services.models

data class RoomRequest(
    val id: String,
    val requestCode: String,
    val note: String,
    val status: String,
    val createdDate: String,
    val updatedDate: String,
    val points: Int,
    val timeMetrics: TimeMetrics
)

data class TimeMetrics(
    val totalTimeMinutes: Int,
    val totalTimeFormatted: String,
    val activitiesCount: Int
)


fun RoomRequestDto.toDomain(): RoomRequest {
    return RoomRequest(
        id = id,
        requestCode = reqCode,
        note = note,
        status = status,
        createdDate = this.created_date,
        updatedDate =this.updated_date,
        points = points.toIntOrNull() ?: 0,
        timeMetrics = timeMetrics.toDomain()
    )
}

fun TimeMetricsDto.toDomain(): TimeMetrics {
    return TimeMetrics(
        totalTimeMinutes = totalTimeMinutes,
        totalTimeFormatted = totalTimeFormatted,
        activitiesCount = activitiesCount
    )
}