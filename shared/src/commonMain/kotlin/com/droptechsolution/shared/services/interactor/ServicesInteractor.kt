package com.droptechsolution.shared.services.interactor

import com.droptechsolution.shared.network.NetworkError
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.services.models.OutletService
import com.droptechsolution.shared.services.models.RequestDetails
import com.droptechsolution.shared.services.models.RequestDetailsRequest
import com.droptechsolution.shared.services.models.RequestSource
import com.droptechsolution.shared.services.models.RoomRequest
import com.droptechsolution.shared.services.models.ServiceRequestRowUi
import com.droptechsolution.shared.services.models.ServicesRequest
import com.droptechsolution.shared.services.models.isSuccessful
import com.droptechsolution.shared.services.models.mergeTaskRows
import com.droptechsolution.shared.services.models.toDetailsUi
import com.droptechsolution.shared.services.models.toDomain
import com.droptechsolution.shared.services.models.toDomainList
import com.droptechsolution.shared.services.models.RequestDetailsUi
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
        if (source == RequestSource.OUTLET) {
            return when (val outletResult = loadOutletServices(outletId)) {
                is NetworkResult.Success -> {
                    val service = outletResult.data.find { it.id == requestId }
                        ?: return NetworkResult.Error(NetworkError.Unknown("Service not found"))
                    NetworkResult.Success(service.toDetailsUi())
                }
                is NetworkResult.Error -> NetworkResult.Error(outletResult.error)
            }
        }

        return when (val result = servicesAPI.getRequestDetails(RequestDetailsRequest(outletId, requestId))) {
            is NetworkResult.Success -> {
                if (result.data.isSuccessful()) {
                    NetworkResult.Success(result.data.toDomain().toUi())
                } else {
                    NetworkResult.Error(NetworkError.Unknown("Unable to load request details"))
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
