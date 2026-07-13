package com.droptechsolution.shared.services.models

import kotlinx.serialization.Serializable

@Serializable
data class ServicesRequest(
    val outletId: String,
)

@Serializable
data class RoomRequestFilter(
    val key: String,
    val value: String,
)

@Serializable
data class RoomRequestsRequest(
    val outletId: String,
    val filterBy: List<RoomRequestFilter> = emptyList(),
)

fun statusFilter(status: String): List<RoomRequestFilter> =
    listOf(RoomRequestFilter(key = "status", value = status))
