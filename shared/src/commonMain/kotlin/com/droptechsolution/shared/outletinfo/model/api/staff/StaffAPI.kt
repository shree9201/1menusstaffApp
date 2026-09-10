package com.droptechsolution.shared.outletinfo.model.api.staff

import com.droptechsolution.shared.network.NetworkClient
import com.droptechsolution.shared.network.NetworkError
import com.droptechsolution.shared.network.NetworkLogger
import com.droptechsolution.shared.network.NETWORK_LOG_TAG
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.network.URN
import io.ktor.client.request.setBody
import kotlinx.serialization.SerializationException

class StaffAPI(
    private val networkClient: NetworkClient,
) {
    suspend fun staffList(
        request: StaffListRequest,
    ): NetworkResult<StaffResponse> =
        networkClient.post("${URN.SERVER}${URN.STAFF_LIST}") {
            setBody(request)
        }

    suspend fun addStaff(
        request: AddStaffRequest,
    ): NetworkResult<StaffMutationResponse> =
        networkClient.post("${URN.SERVER}${URN.STAFF_ADD}") {
            setBody(request)
        }

    suspend fun updateStaff(
        request: UpdateStaffRequest,
    ): NetworkResult<StaffMutationResponse> =
        networkClient.post("${URN.SERVER}${URN.STAFF_UPDATE}") {
            setBody(request)
        }

    suspend fun deleteStaff(
        request: DeleteStaffRequest,
    ): NetworkResult<DeleteStaffResponse> =
        networkClient.post("${URN.SERVER}${URN.STAFF_DELETE}") {
            setBody(request)
        }

    suspend fun staffAttendances(
        request: StaffAttendancesRequest,
    ): NetworkResult<StaffAttendancesResponse> =
        networkClient.post("${URN.SERVER}${URN.STAFF_ATTENDANCES}") {
            setBody(request)
        }

    suspend fun changePassword(
        request: ChangePasswordRequest,
    ): NetworkResult<ChangePasswordResponse> =
        when (val result = networkClient.postForText("${URN.SERVER}${URN.CHANGE_PASSWORD}") {
            setBody(request)
        }) {
            is NetworkResult.Success -> {
                try {
                    NetworkResult.Success(parseChangePasswordResponse(result.data))
                } catch (e: SerializationException) {
                    NetworkLogger.e(
                        tag = NETWORK_LOG_TAG,
                        message = "Failed to parse changePassword response: ${e.message}. Body: ${result.data}",
                        throwable = e,
                    )
                    NetworkResult.Error(
                        NetworkError.Serialization(
                            message = e.message ?: "Failed to parse response",
                            cause = e,
                        )
                    )
                }
            }
            is NetworkResult.Error -> result
        }

    suspend fun updateProfile(
        request: UpdateProfileRequest,
    ): NetworkResult<UpdateProfileResponse> =
        when (val result = networkClient.postForText("${URN.SERVER}${URN.UPDATE_PROFILE}") {
            setBody(request)
        }) {
            is NetworkResult.Success -> {
                try {
                    NetworkResult.Success(parseUpdateProfileResponse(result.data))
                } catch (e: SerializationException) {
                    NetworkLogger.e(
                        tag = NETWORK_LOG_TAG,
                        message = "Failed to parse updateProfile response: ${e.message}. Body: ${result.data}",
                        throwable = e,
                    )
                    NetworkResult.Error(
                        NetworkError.Serialization(
                            message = e.message ?: "Failed to parse response",
                            cause = e,
                        )
                    )
                }
            }
            is NetworkResult.Error -> result
        }
}
