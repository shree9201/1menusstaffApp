package com.droptechsolution.shared.services.services

import com.droptechsolution.shared.network.NetworkClient
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.network.URN
import com.droptechsolution.shared.services.models.OutletServicesResponse
import com.droptechsolution.shared.services.models.RequestDetailsRequest
import com.droptechsolution.shared.services.models.RequestDetailsResponse
import com.droptechsolution.shared.services.models.RoomRequestsResponse
import com.droptechsolution.shared.services.models.ServicesRequest
import com.droptechsolution.shared.services.models.UpdateRequestBody
import com.droptechsolution.shared.services.models.UpdateRequestResponse
import io.ktor.client.request.setBody

interface IServicesAPI {
    suspend fun getRoomRequests(request: ServicesRequest): NetworkResult<RoomRequestsResponse>
    suspend fun getOutletServices(request: ServicesRequest): NetworkResult<OutletServicesResponse>
    suspend fun getRequestDetails(request: RequestDetailsRequest): NetworkResult<RequestDetailsResponse>
    suspend fun updateRequest(request: UpdateRequestBody): NetworkResult<UpdateRequestResponse>
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

    override suspend fun getRequestDetails(
        request: RequestDetailsRequest,
    ): NetworkResult<RequestDetailsResponse> =
        networkClient.post("${URN.SERVER}${URN.GET_REQUEST_DETAILS}") {
            setBody(request)
        }

    override suspend fun updateRequest(
        request: UpdateRequestBody,
    ): NetworkResult<UpdateRequestResponse> =
        networkClient.post("${URN.SERVER}${URN.UPDATE_REQUEST}") {
            setBody(request)
        }
}
