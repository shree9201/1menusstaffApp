package com.droptechsolution.shared.services.models

import kotlinx.serialization.Serializable

@Serializable
data class RoomRequestDto(
    val id: String,
    val reqCode: String,
    val title: String? = null,
    val userId: String,
    val roomId: String,
    val serviceId: String,
    val assigned: String,
    val start_time: String? = null,
    val end_time: String? = null,
    val date: String,
    val ip: String,
    val additionalField: String? = null,
    val audio: String? = null,
    val note: String? = null,
    val status: String,
    val escalated_by: String? = null,
    val created_date: String,
    val updated_date: String,
    val guestId: String? = null,
    val guestName: String? = null,
    val guestMobile: String? = null,
    val guestCode: String? = null,
    val points: String,
    val serviceDetails: List<OutletServiceDto> = emptyList(),
    val timeMetrics: TimeMetricsDto? = null,
)

@Serializable
data class TimeMetricsDto(
    val totalTimeMinutes: Double = 0.0,
    val totalTimeFormatted: String = "",
    val activitiesCount: Int = 0,
    val staffInvolved: List<String> = emptyList(),
    val timeline: List<RoomRequestTimelineDto> = emptyList(),
    val averageTimePerActivityMinutes: Double = 0.0,
)

@Serializable
data class RoomRequestTimelineDto(
    val userId: String? = null,
    val assignedTo: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val durationMinutes: Double? = null,
    val durationFormatted: String? = null,
)
