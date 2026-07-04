package com.droptechsolution.shared.services.models

import kotlinx.serialization.Serializable

@Serializable
data class RoomRequestsResponse(
    val status: Boolean,
    val value: String,
    val count: Int = 0,
    val roomRequest: List<RoomRequestDto> = emptyList(),
)

@Serializable
data class OutletServicesResponse(
    val status: Boolean,
    val value: String,
    val count: Int = 0,
    val services: List<OutletServiceDto> = emptyList(),
)

@Serializable
data class OutletServiceDto(
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
    val points: String,
    val onHoldOption: String,
    val status: String,
    val created_date: String,
    val updated_date: String,
)
