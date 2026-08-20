package com.droptechsolution.shared.outletinfo.model.api.staff

import kotlinx.serialization.Serializable

@Serializable
data class StaffAttendancesRequest(
    val outletId: Int,
    val staffId: Int,
)

@Serializable
data class StaffAttendancesResponse(
    val status: String,
    val value: String,
    val count: Int = 0,
    val attendanceDetails: List<StaffAttendanceDetail> = emptyList(),
)

@Serializable
data class StaffAttendanceDetail(
    val staffId: String,
    val date: String,
    val date_time: String,
    val login_date_time: String,
    val logout_date_time: String,
    val created_date: String,
    val updated_date: String,
    val totalWorkingHours: String,
    val totalWorkingMinutes: Double,
)
