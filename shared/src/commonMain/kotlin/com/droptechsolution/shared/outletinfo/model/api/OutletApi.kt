package com.droptechsolution.shared.outletinfo.model.api

import com.droptechsolution.shared.network.NetworkClient
import com.droptechsolution.shared.network.NetworkError
import com.droptechsolution.shared.network.NetworkLogger
import com.droptechsolution.shared.network.NETWORK_LOG_TAG
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.network.URN
import com.droptechsolution.shared.outletinfo.model.api.staff.NotificationRequest
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffLoginRequest
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffLogoutRequest
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffLogoutResponse
import io.ktor.client.request.setBody
import kotlinx.serialization.SerializationException

class OutletApi(
    private val networkClient: NetworkClient,
) {

    suspend fun getOutletInfo(
        request: OutletRequest,
    ): NetworkResult<List<OutletResponse>> =
        networkClient.post("https://1menus.com/app/API/outletInfo") {
            setBody(request)
        }

    suspend fun staffLogin(
        request: StaffLoginRequest,
    ): NetworkResult<StaffLoginResult> =
        when (val result = networkClient.postForText("${URN.SERVER}${URN.STAFF_LOGIN}") {
            setBody(request)
        }) {
            is NetworkResult.Success -> {
                try {
                    NetworkResult.Success(parseStaffLoginResponse(result.data))
                } catch (e: SerializationException) {
                    NetworkLogger.e(
                        tag = NETWORK_LOG_TAG,
                        message = "Failed to parse staffLogin response: ${e.message}. Body: ${result.data}",
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

    suspend fun staffLogout(
        request: StaffLogoutRequest,
    ): NetworkResult<StaffLogoutResponse> =
        networkClient.post("${URN.SERVER}${URN.STAFF_LOGOUT}") {
            setBody(request)
        }

    suspend fun sendNotification(
        request: NotificationRequest,
    ): NetworkResult<String> =
        networkClient.post("${URN.SERVER}/${URN.SEND_NOTIFICATION}") {
            setBody(request)
        }
}
