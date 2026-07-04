package com.droptechsolution.shared.services.models

import kotlinx.serialization.Serializable

@Serializable
data class RequestDetailsRequest(
    val outletId: String,
    val requestId: String,
)

@Serializable
data class RequestActivityDto(
    val dateTime: String,
    val status: String,
    val comment: String,
    val created_date: String? = null,
    val assignedTo: String? = null,
    val assignedMobile: String? = null,
)

@Serializable
data class RequestDetailsResponse(
    val status: String,
    val value: RoomRequestDto,
    val activity: List<RequestActivityDto> = emptyList(),
)

fun RequestDetailsResponse.isSuccessful(): Boolean =
    status.equals("true", ignoreCase = true) || status == "1"

fun RequestDetailsResponse.toDomain(): RequestDetails =
    RequestDetails(
        request = value.toDomain(),
        activities = activity.map { it.toDomain() },
    )

fun RequestActivityDto.toDomain(): RequestActivity =
    RequestActivity(
        dateTime = dateTime,
        status = status,
        comment = comment,
        createdDate = created_date.orEmpty(),
        assignedTo = assignedTo.orEmpty(),
        assignedMobile = assignedMobile.orEmpty(),
    )
