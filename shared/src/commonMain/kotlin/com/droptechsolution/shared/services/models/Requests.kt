package com.droptechsolution.shared.services.models

import kotlinx.serialization.Serializable

@Serializable
data class ServicesRequest(
    val outletId: String,
    val filterBy: List<RoomRequestFilter> = emptyList(),
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
    val limit: String? = null,
)

data class StaffRequestScope(
    val loginUserType: String? = null,
    val staffId: String? = null,
) {
    fun assignedFilters(): List<RoomRequestFilter> {
        if (!loginUserType.equals("STAFF", ignoreCase = true)) return emptyList()
        val assignedId = staffId?.takeIf { it.isNotBlank() } ?: return emptyList()
        return listOf(RoomRequestFilter(key = "assigned", value = assignedId))
    }

    companion object {
        val Empty = StaffRequestScope()
    }
}

fun List<RoomRequestFilter>.withStaffScope(staffScope: StaffRequestScope): List<RoomRequestFilter> =
    staffScope.assignedFilters() + this

fun apiStatusFilterValue(status: String): String = when (status.uppercase()) {
    "ACCEPT" -> "ACCEPTED"
    "START" -> "STARTED"
    "CLOSED" -> "CLOSED"
    "PAUSE", "PAUSED" -> "HOLD"
    else -> status.uppercase()
}

fun statusFilter(status: String): List<RoomRequestFilter> =
    listOf(RoomRequestFilter(key = "status", value = apiStatusFilterValue(status)))
