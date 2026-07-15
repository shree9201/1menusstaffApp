package com.droptechsolution.shared.services.models

data class RoomRequest(
    val id: String,
    val requestCode: String,
    val title: String,
    val roomId: String,
    val serviceId: String,
    val assigned: String,
    val note: String,
    val status: String,
    val createdDate: String,
    val updatedDate: String,
    val points: Int,
    val guestName: String,
    val startTime: String = "",
    val endTime: String = "",
    val timeMetrics: TimeMetrics,
    val serviceDetails: List<OutletService> = emptyList(),
)

data class TimeMetrics(
    val totalTimeMinutes: Double,
    val totalTimeFormatted: String,
    val activitiesCount: Int,
)

data class OutletService(
    val id: String,
    val title: String,
    val userId: String,
    val boxId: String,
    val serviceId: String,
    val actionBy: String,
    val aksDateTime: String,
    val sq: String? = null,
    val information: String,
    val reminderTime: String,
    val escalationTime: String,
    val priority: String,
    val points: Int,
    val onHoldOption: String,
    val status: String,
    val createdDate: String,
    val updatedDate: String,
    val icon: String = "",
)

data class RequestDetails(
    val request: RoomRequest,
    val activities: List<RequestActivity>,
    val serviceDetails: OutletService? = null,
)

data class RequestActivity(
    val dateTime: String,
    val status: String,
    val comment: String,
    val createdDate: String,
    val assignedTo: String,
    val assignedMobile: String,
)

fun RoomRequestDto.toDomain(): RoomRequest {
    return RoomRequest(
        id = id,
        requestCode = reqCode,
        title = title.orEmpty(),
        roomId = roomId,
        serviceId = serviceId,
        assigned = assigned,
        note = note.orEmpty(),
        status = status,
        createdDate = created_date,
        updatedDate = updated_date,
        points = points.toIntOrNull() ?: 0,
        guestName = guestName.orEmpty(),
        startTime = start_time.orEmpty(),
        endTime = end_time.orEmpty(),
        timeMetrics = timeMetrics?.toDomain() ?: TimeMetrics(
            totalTimeMinutes = 0.0,
            totalTimeFormatted = "",
            activitiesCount = 0,
        ),
        serviceDetails = serviceDetails.map { it.toDomain() },
    )
}

fun TimeMetricsDto.toDomain(): TimeMetrics {
    return TimeMetrics(
        totalTimeMinutes = totalTimeMinutes,
        totalTimeFormatted = totalTimeFormatted,
        activitiesCount = activitiesCount,
    )
}

fun OutletServiceDto.toDomain(): OutletService {
    return OutletService(
        id = id,
        title = title,
        userId = userId,
        boxId = boxId,
        serviceId = serviceId,
        actionBy = actionBy,
        aksDateTime = aksDateTime,
        sq = sq,
        information = information,
        reminderTime = reminderTime,
        escalationTime = escalationTime,
        priority = priority,
        points = points.toIntOrNull() ?: 0,
        onHoldOption = onHoldOption,
        status = status,
        createdDate = created_date,
        updatedDate = updated_date,
        icon = icon.orEmpty(),
    )
}

fun RoomRequestsResponse.toDomainList(): List<RoomRequest> =
    roomRequest.map { it.toDomain() }

fun OutletServicesResponse.toDomainList(): List<OutletService> =
    services.map { it.toDomain() }
