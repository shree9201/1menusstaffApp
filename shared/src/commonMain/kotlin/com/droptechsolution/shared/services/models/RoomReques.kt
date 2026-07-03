package com.droptechsolution.shared.services.models

import kotlinx.serialization.Serializable

@Serializable
data class RoomRequestDto(
    val id: String,
    val reqCode: String,
    val title: String,
    val userId: String,
    val roomId: String,
    val serviceId: String,
    val assigned: String,
    val start_time: String? = null,
    val end_time: String? = null,
    val date: String,
    val ip: String,
    val additionalField: String,
    val audio: String? = null,
    val note: String,
    val status: String,
    val escalated_by: String? = null,
    val created_date: String,
    val updated_date: String,
    val guestId: String? = null,
    val guestName: String? = null,
    val guestMobile: String? = null,
    val guestCode: String? = null,
    val points: String,
    val timeMetrics: TimeMetricsDto
)

@Serializable
data class TimeMetricsDto(
    val totalTimeMinutes: Int,
    val totalTimeFormatted: String,
    val activitiesCount: Int,
    val staffInvolved: List<String> = emptyList(),
    val timeline: List<RoomRequestTimelineDto> = emptyList(),
    val averageTimePerActivityMinutes: Int
)

@Serializable
data class RoomRequestTimelineDto(
    val id: String? = null,
    val status: String? = null,
    val staffId: String? = null,
    val createdDate: String? = null
)