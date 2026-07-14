package com.droptechsolution.shared.ui.staff.models

import com.droptechsolution.shared.outletinfo.model.api.staff.StaffDetails

enum class StaffAvailability {
    Available,
    Busy,
    Offline,
}

data class StaffMemberUi(
    val id: String,
    val name: String,
    val subtitle: String,
    val availability: StaffAvailability,
    val badgeLabel: String,
)

data class StaffSummaryUi(
    val availableCount: Int,
    val busyCount: Int,
)

fun StaffDetails.toStaffMemberUi(): StaffMemberUi {
    val availability = resolveAvailability(online, status)
    val subtitle = when (availability) {
        StaffAvailability.Busy -> {
            val detail = status.trim().takeIf { it.isNotBlank() && !it.equals("busy", ignoreCase = true) }
            if (detail != null) "Busy · $detail" else "Busy"
        }
        StaffAvailability.Available -> "Available · On duty"
        StaffAvailability.Offline -> "Offline"
    }
    val badgeLabel = when (availability) {
        StaffAvailability.Available -> "On duty"
        StaffAvailability.Busy -> "Busy"
        StaffAvailability.Offline -> "Offline"
    }
    return StaffMemberUi(
        id = id,
        name = name,
        subtitle = subtitle,
        availability = availability,
        badgeLabel = badgeLabel,
    )
}

private fun resolveAvailability(online: String, status: String): StaffAvailability {
    val isOnline = online.equals("1", ignoreCase = true) ||
        online.equals("yes", ignoreCase = true) ||
        online.equals("true", ignoreCase = true) ||
        online.equals("online", ignoreCase = true)

    if (!isOnline) return StaffAvailability.Offline

    val normalizedStatus = status.trim()
    if (normalizedStatus.isBlank() ||
        normalizedStatus.equals("available", ignoreCase = true) ||
        normalizedStatus.equals("free", ignoreCase = true)
    ) {
        return StaffAvailability.Available
    }

    return StaffAvailability.Busy
}

fun List<StaffMemberUi>.toSummary(): StaffSummaryUi =
    StaffSummaryUi(
        availableCount = count { it.availability == StaffAvailability.Available },
        busyCount = count { it.availability == StaffAvailability.Busy },
    )
