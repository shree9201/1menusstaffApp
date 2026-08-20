package com.droptechsolution.shared.ui.staff.interactor

import com.droptechsolution.shared.network.NetworkError
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffAPI
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffAttendanceDetail
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffAttendancesRequest
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffDetails
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffListRequest

class StaffInteractor(
    private val staffAPI: StaffAPI,
) {
    suspend fun loadStaffList(outletId: String): NetworkResult<List<StaffDetails>> =
        when (val result = staffAPI.staffList(StaffListRequest(outletId))) {
            is NetworkResult.Success -> NetworkResult.Success(result.data.value)
            is NetworkResult.Error -> result
        }

    suspend fun loadStaffAttendances(
        outletId: String,
        staffId: String,
    ): NetworkResult<List<StaffAttendanceDetail>> {
        val outletIdInt = outletId.toIntOrNull()
            ?: return invalidIdError("Invalid outlet ID")
        val staffIdInt = staffId.toIntOrNull()
            ?: return invalidIdError("Invalid staff ID")

        return when (
            val result = staffAPI.staffAttendances(
                StaffAttendancesRequest(
                    outletId = outletIdInt,
                    staffId = staffIdInt,
                ),
            )
        ) {
            is NetworkResult.Success -> NetworkResult.Success(result.data.attendanceDetails)
            is NetworkResult.Error -> result
        }
    }

    private fun invalidIdError(message: String): NetworkResult.Error =
        NetworkResult.Error(NetworkError.Unknown(message))
}
