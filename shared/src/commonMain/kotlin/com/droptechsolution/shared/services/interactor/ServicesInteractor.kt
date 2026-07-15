package com.droptechsolution.shared.services.interactor

import com.droptechsolution.shared.network.NetworkError
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.services.models.OutletService
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffDetails
import com.droptechsolution.shared.services.models.RequestAction
import com.droptechsolution.shared.services.models.RequestDetailsRequest
import com.droptechsolution.shared.services.models.RequestDetailsUi
import com.droptechsolution.shared.services.models.RoomRequest
import com.droptechsolution.shared.services.models.RoomRequestFilter
import com.droptechsolution.shared.services.models.RoomRequestsRequest
import com.droptechsolution.shared.services.models.ServiceRequestRowUi
import com.droptechsolution.shared.services.models.DepartmentOverviewStatUi
import com.droptechsolution.shared.services.models.ServicesRequest
import com.droptechsolution.shared.services.models.UpdateRequestBody
import com.droptechsolution.shared.services.models.apiStatus
import com.droptechsolution.shared.services.models.defaultComment
import com.droptechsolution.shared.services.models.isSuccessful
import com.droptechsolution.shared.services.models.mergeTaskRows
import com.droptechsolution.shared.services.models.statusFilter
import com.droptechsolution.shared.services.models.toDepartmentOverviewStats
import com.droptechsolution.shared.services.models.toDomain
import com.droptechsolution.shared.services.models.toDomainList
import com.droptechsolution.shared.services.models.toTaskRows
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

    suspend fun loadServiceStatusCounts(outletId: String): NetworkResult<List<DepartmentOverviewStatUi>> =
        when (val result = servicesAPI.getServiceStatusCount(ServicesRequest(outletId))) {
            is NetworkResult.Success -> {
                if (result.data.status) {
                    NetworkResult.Success(result.data.toDepartmentOverviewStats())
                } else {
                    NetworkResult.Error(NetworkError.Unknown("Unable to load service statistics"))
                }
            }
            is NetworkResult.Error -> result
        }

    suspend fun loadRoomRequestsForStatuses(
        outletId: String,
        statuses: List<String>,
    ): NetworkResult<List<RoomRequest>> {
        if (statuses.isEmpty()) return loadRoomRequests(outletId)

        val merged = linkedMapOf<String, RoomRequest>()
        for (status in statuses) {
            when (val result = loadRoomRequests(outletId, statusFilter(status))) {
                is NetworkResult.Success -> result.data.forEach { merged[it.id] = it }
                is NetworkResult.Error -> return result
            }
        }
        return NetworkResult.Success(merged.values.toList())
    }

    suspend fun loadRoomRequests(
        outletId: String,
        filters: List<RoomRequestFilter> = emptyList(),
    ): NetworkResult<List<RoomRequest>> =
        when (val result = servicesAPI.getRoomRequests(RoomRequestsRequest(outletId, filters))) {
            is NetworkResult.Success -> mapResponse(result.data.status, result.data.toDomainList())
            is NetworkResult.Error -> result
        }

    suspend fun loadAllTasks(
        outletId: String,
        activeOnly: Boolean = false,
        statusFilter: String? = null,
        statusFilters: List<String> = emptyList(),
    ): NetworkResult<TaskListData> {
        val filters = statusFilter?.let { statusFilter(it) }.orEmpty()
        val roomResult = when {
            statusFilters.isNotEmpty() -> loadRoomRequestsForStatuses(outletId, statusFilters)
            else -> loadRoomRequests(outletId, filters)
        }

        val roomRequests = when (roomResult) {
            is NetworkResult.Success -> roomResult.data
            is NetworkResult.Error -> return roomResult
        }

        val rows = if (statusFilter.isNullOrBlank() && statusFilters.isEmpty()) {
            mergeTaskRows(
                roomRequests = roomRequests,
                outletServices = emptyList(),
                includeOutletServices = false,
                activeOnly = activeOnly,
            )
        } else {
            roomRequests.toTaskRows()
        }

        return NetworkResult.Success(
            TaskListData(
                roomRequests = roomRequests,
                outletServices = emptyList(),
                rows = rows,
            ),
        )
    }

    suspend fun loadRequestDetails(
        outletId: String,
        requestId: String,
    ): NetworkResult<RequestDetailsUi> =
        when (val result = servicesAPI.getRequestDetails(RequestDetailsRequest(outletId, requestId))) {
            is NetworkResult.Success -> {
                if (result.data.isSuccessful()) {
                    NetworkResult.Success(result.data.toDomain().toUi())
                } else {
                    NetworkResult.Error(NetworkError.Unknown("Unable to load request details"))
                }
            }
            is NetworkResult.Error -> result
        }

    suspend fun updateRequest(
        outletId: String,
        requestId: String,
        action: RequestAction,
        user: StaffDetails,
        staffName: String = user.name,
    ): NetworkResult<RequestDetailsUi> {
        val body = UpdateRequestBody(
            outletId = outletId,
            requestId = requestId,
            status = action.apiStatus(),
            assignedTo = user.id,
            comment = action.defaultComment(staffName),
            user = user,
        )

        return when (val result = servicesAPI.updateRequest(body)) {
            is NetworkResult.Success -> {
                if (!result.data.isSuccessful()) {
                    return NetworkResult.Error(NetworkError.Unknown("Unable to update request"))
                }
                val embeddedDetails = result.data.requestDetails
                if (embeddedDetails != null && embeddedDetails.isSuccessful()) {
                    NetworkResult.Success(embeddedDetails.toDomain().toUi())
                } else {
                    loadRequestDetails(outletId, requestId)
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
