package com.droptechsolution.shared.outletinfo.model.api.staff

import com.droptechsolution.shared.network.NetworkClient
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.network.URN
import io.ktor.client.request.setBody

class StaffAPI(
    private val networkClient: NetworkClient,
) {
    suspend fun staffList(
        request: StaffListRequest,
    ): NetworkResult<StaffResponse> =
        networkClient.post("${URN.SERVER}${URN.STAFF_LIST}") {
            setBody(request)
        }
}
