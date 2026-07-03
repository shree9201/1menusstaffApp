package com.droptechsolution.shared.outletinfo.model.api

import com.droptechsolution.shared.network.NetworkClient
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.network.URN
import com.droptechsolution.shared.outletinfo.model.api.staff.NotificationRequest
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffLoginRequest
import io.ktor.client.request.setBody

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
    ): NetworkResult<StaffDetailsResponse> =
        networkClient.post("${URN.SERVER}${URN.STAFF_LOGIN}") {
            setBody(request)
        }

    suspend fun sendNotification(
        request: NotificationRequest,
    ): NetworkResult<String> =
        networkClient.post("${URN.SERVER}/${URN.SEND_NOTIFICATION}") {
            setBody(request)
        }
}
