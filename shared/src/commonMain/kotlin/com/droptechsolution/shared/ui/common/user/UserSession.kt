package com.droptechsolution.shared.ui.common.user

import androidx.compose.runtime.staticCompositionLocalOf
import com.droptechsolution.shared.masterdata.models.MasterData
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffDetails

data class UserSession(
    val staffId: String,
    val staffName: String,
    val staffType: StaffType,
    val staffTypeLabel: String,
    val department: StaffDepartment,
    val departmentLabel: String,
)

val LocalUserSession = staticCompositionLocalOf<UserSession?> { null }

fun StaffDetails.toUserSession(masterData: MasterData? = null): UserSession {
    val staffType = type.toStaffType()
    val staffDepartment = department.orEmpty().toStaffDepartment()

    val staffTypeLabel = masterData?.staffTypes
        ?.firstOrNull { it.key.equals(staffType.apiKey, ignoreCase = true) }
        ?.value
        ?: staffType.defaultLabel()

    val departmentLabel = masterData?.departments
        ?.firstOrNull { it.key.equals(staffDepartment.apiKey, ignoreCase = true) }
        ?.value
        ?: staffDepartment.defaultLabel()

    return UserSession(
        staffId = id,
        staffName = name,
        staffType = staffType,
        staffTypeLabel = staffTypeLabel,
        department = staffDepartment,
        departmentLabel = departmentLabel,
    )
}
