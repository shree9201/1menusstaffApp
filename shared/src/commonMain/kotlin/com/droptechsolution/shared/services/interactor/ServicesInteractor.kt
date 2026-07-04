package com.droptechsolution.shared.services.interactor

import com.droptechsolution.shared.network.NetworkError
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.services.models.OutletService
import com.droptechsolution.shared.services.models.RequestAction
import com.droptechsolution.shared.services.models.RequestDetailsRequest
import com.droptechsolution.shared.services.models.RequestDetailsUi
import com.droptechsolution.shared.services.models.RequestSource
import com.droptechsolution.shared.services.models.RoomRequest
import com.droptechsolution.shared.services.models.ServiceRequestRowUi
import com.droptechsolution.shared.services.models.ServicesRequest
import com.droptechsolution.shared.services.models.UpdateRequestBody
import com.droptechsolution.shared.services.models.apiStatus
import com.droptechsolution.shared.services.models.defaultComment
import com.droptechsolution.shared.services.models.isSuccessful
import com.droptechsolution.shared.services.models.mergeTaskRows
import com.droptechsolution.shared.services.models.toDetailsUi
import com.droptechsolution.shared.services.models.toDomain
import com.droptechsolution.shared.services.models.toDomainList
import com.droptechsolution.shared.services.models.toUi
import com.droptechsolution.shared.services.services.IServicesAPI

data class TaskListData(
    val roomRequests: List<RoomRequest>,
    val outletServices: List<OutletService>,
    val rows: List<ServiceRequestRowUi>,
)

class ServicesInteractor(
    private val servicesAPI: IServicesAPI,
) {

    suspend fun loadRoomRequests(outletId: String): NetworkResult<List<RoomRequest>> =
        when (val result = servicesAPI.getRoomRequests(ServicesRequest(outletId))) {
            is NetworkResult.Success -> mapResponse(result.data.status, result.data.toDomainList())
            is NetworkResult.Error -> result
        }

    suspend fun loadOutletServices(outletId: String): NetworkResult<List<OutletService>> =
        when (val result = servicesAPI.getOutletServices(ServicesRequest(outletId))) {
            is NetworkResult.Success -> mapResponse(result.data.status, result.data.toDomainList())
            is NetworkResult.Error -> result
        }

    suspend fun loadAllTasks(
        outletId: String,
        activeOnly: Boolean = false,
    ): NetworkResult<TaskListData> {
        val roomResult = loadRoomRequests(outletId)
        val outletResult = loadOutletServices(outletId)

        val roomRequests = when (roomResult) {
            is NetworkResult.Success -> roomResult.data
            is NetworkResult.Error -> return roomResult
        }
        val outletServices = when (outletResult) {
            is NetworkResult.Success -> outletResult.data
            is NetworkResult.Error -> emptyList()
        }

        return NetworkResult.Success(
            TaskListData(
                roomRequests = roomRequests,
                outletServices = outletServices,
                rows = mergeTaskRows(
                    roomRequests = roomRequests,
                    outletServices = outletServices,
                    activeOnly = activeOnly,
                ),
            ),
        )
    }

    suspend fun loadRequestDetails(
        outletId: String,
        requestId: String,
        source: RequestSource,
    ): NetworkResult<RequestDetailsUi> {
        val outletServices = when (val outletResult = loadOutletServices(outletId)) {
            is NetworkResult.Success -> outletResult.data
            is NetworkResult.Error -> emptyList()
        }

        if (source == RequestSource.OUTLET) {
            val service = outletServices.find { it.id == requestId }
                ?: return NetworkResult.Error(NetworkError.Unknown("Service not found"))
            return NetworkResult.Success(service.toDetailsUi())
        }

        return when (val result = servicesAPI.getRequestDetails(RequestDetailsRequest(outletId, requestId))) {
            is NetworkResult.Success -> {
                if (result.data.isSuccessful()) {
                    val outletService = outletServices.find { it.serviceId == result.data.value.serviceId }
                    NetworkResult.Success(result.data.toDomain().toUi(outletService))
                } else {
                    NetworkResult.Error(NetworkError.Unknown("Unable to load request details"))
                }
            }
            is NetworkResult.Error -> result
        }
    }

    suspend fun updateRequest(
        outletId: String,
        requestId: String,
        action: RequestAction,
        assignedTo: String,
        staffName: String,
    ): NetworkResult<RequestDetailsUi> {
        val body = UpdateRequestBody(
            outletId = outletId,
            requestId = requestId,
            status = action.apiStatus(),
            assignedTo = assignedTo,
            comment = action.defaultComment(staffName),
        )

        return when (val result = servicesAPI.updateRequest(body)) {
            is NetworkResult.Success -> {
                if (!result.data.isSuccessful()) {
                    return NetworkResult.Error(NetworkError.Unknown("Unable to update request"))
                }
                val embeddedDetails = result.data.requestDetails
                if (embeddedDetails != null && embeddedDetails.isSuccessful()) {
                    val outletServices = when (val outletResult = loadOutletServices(outletId)) {
                        is NetworkResult.Success -> outletResult.data
                        is NetworkResult.Error -> emptyList()
                    }
                    val outletService = outletServices.find { it.serviceId == embeddedDetails.value.serviceId }
                    NetworkResult.Success(embeddedDetails.toDomain().toUi(outletService))
                } else {
                    loadRequestDetails(outletId, requestId, RequestSource.ROOM)
                }
            }
            is NetworkResult.Error -> result
        }
    }

    private fun <T> mapResponse(
        status: Boolean,
        items: List<T>,
    ): NetworkResult<List<T>> =
        if (status) {
            NetworkResult.Success(items)
        } else {
            NetworkResult.Error(NetworkError.Unknown("No results found"))
        }
}
