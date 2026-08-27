package com.droptechsolution.shared.ui.staff.interactor

import com.droptechsolution.shared.network.NetworkError
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.outletinfo.model.api.staff.ChangePasswordRequest
import com.droptechsolution.shared.outletinfo.model.api.staff.UpdateProfileRequest
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffAPI
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffAttendanceDetail
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffAttendancesRequest
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffDetails
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffListRequest
import com.droptechsolution.shared.outletinfo.model.api.staff.isSuccessful

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

    suspend fun changePassword(
        outletId: String,
        staffId: String,
        oldPassword: String,
        newPassword: String,
    ): NetworkResult<String> {
        val outletIdInt = outletId.toIntOrNull()
            ?: return invalidIdError("Invalid outlet ID")
        val staffIdInt = staffId.toIntOrNull()
            ?: return invalidIdError("Invalid staff ID")

        return when (
            val result = staffAPI.changePassword(
                ChangePasswordRequest(
                    outletId = outletIdInt,
                    staffId = staffIdInt,
                    oldPassword = oldPassword,
                    newPassword = newPassword,
                ),
            )
        ) {
            is NetworkResult.Success -> {
                if (result.data.isSuccessful()) {
                    NetworkResult.Success(
                        result.data.value?.takeIf { it.isNotBlank() } ?: "Password changed successfully",
                    )
                } else {
                    NetworkResult.Error(
                        NetworkError.Unknown(
                            result.data.value?.takeIf { it.isNotBlank() } ?: "Unable to change password",
                        ),
                    )
                }
            }
            is NetworkResult.Error -> result
        }
    }

    suspend fun updateProfile(
        outletId: String,
        staffId: String,
        name: String,
        email: String,
        mobile: String,
        username: String,
        customisedPosition: String,
    ): NetworkResult<String> {
        val outletIdInt = outletId.toIntOrNull()
            ?: return invalidIdError("Invalid outlet ID")
        val staffIdInt = staffId.toIntOrNull()
            ?: return invalidIdError("Invalid staff ID")

        return when (
            val result = staffAPI.updateProfile(
                UpdateProfileRequest(
                    outletId = outletIdInt,
                    staffId = staffIdInt,
                    name = name,
                    email = email,
                    mobile = mobile,
                    username = username,
                    customised_position = customisedPosition,
                ),
            )
        ) {
            is NetworkResult.Success -> {
                if (result.data.isSuccessful()) {
                    NetworkResult.Success(
                        result.data.value?.takeIf { it.isNotBlank() } ?: "Profile updated successfully",
                    )
                } else {
                    NetworkResult.Error(
                        NetworkError.Unknown(
                            result.data.value?.takeIf { it.isNotBlank() } ?: "Unable to update profile",
                        ),
                    )
                }
            }
            is NetworkResult.Error -> result
        }
    }

    private fun invalidIdError(message: String): NetworkResult.Error =
        NetworkResult.Error(NetworkError.Unknown(message))
}
