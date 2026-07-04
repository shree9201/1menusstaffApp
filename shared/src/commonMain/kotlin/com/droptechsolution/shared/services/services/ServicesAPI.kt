package com.droptechsolution.shared.services.services

import com.droptechsolution.shared.network.NetworkClient
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.network.URN
import com.droptechsolution.shared.services.models.OutletServicesResponse
import com.droptechsolution.shared.services.models.RoomRequestsResponse
import com.droptechsolution.shared.services.models.ServicesRequest
import io.ktor.client.request.setBody

interface IServicesAPI {
    suspend fun getRoomRequests(request: ServicesRequest): NetworkResult<RoomRequestsResponse>
    suspend fun getOutletServices(request: ServicesRequest): NetworkResult<OutletServicesResponse>
}

class ServicesAPI(
    private val networkClient: NetworkClient,
) : IServicesAPI {

    override suspend fun getRoomRequests(
        request: ServicesRequest,
    ): NetworkResult<RoomRequestsResponse> =
        networkClient.post("${URN.SERVER}${URN.GET_ROOM_REQUESTS}") {
            setBody(request)
        }

    override suspend fun getOutletServices(
        request: ServicesRequest,
    ): NetworkResult<OutletServicesResponse> =
        networkClient.post("${URN.SERVER}${URN.GET_OUTLET_SERVICES}") {
            setBody(request)
        }
}
