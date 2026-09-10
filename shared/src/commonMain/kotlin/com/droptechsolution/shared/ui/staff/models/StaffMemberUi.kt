package com.droptechsolution.shared.ui.staff.models

import com.droptechsolution.shared.outletinfo.model.api.staff.StaffDetails
import com.droptechsolution.shared.ui.common.user.toInitials
import com.droptechsolution.shared.ui.common.user.toStaffDepartment
import com.droptechsolution.shared.ui.common.user.toStaffType

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

data class StaffDetailsUi(
    val id: String,
    val name: String,
    val initials: String,
    val type: String,
    val roleLabel: String,
    val department: String,
    val departmentLabel: String,
    val username: String,
    val email: String,
    val mobile: String,
    val position: String,
    val status: String,
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

fun StaffDetails.toStaffDetailsUi(): StaffDetailsUi {
    val member = toStaffMemberUi()
    val roleLabel = customised_position?.takeIf { it.isNotBlank() }
        ?: type.toStaffType().defaultLabel()
    val departmentLabel = department.orEmpty().toStaffDepartment().defaultLabel()
        .ifBlank { department.orEmpty() }
    return StaffDetailsUi(
        id = id,
        name = name,
        initials = name.toInitials(),
        type = type,
        roleLabel = roleLabel,
        department = department.orEmpty(),
        departmentLabel = departmentLabel.ifBlank { "—" },
        username = username.ifBlank { "—" },
        email = email.orEmpty().ifBlank { "—" },
        mobile = mobile.orEmpty().ifBlank { "—" },
        position = customised_position.orEmpty().ifBlank { roleLabel },
        status = status.ifBlank { member.badgeLabel },
        availability = member.availability,
        badgeLabel = member.badgeLabel,
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
