package com.droptechsolution.shared.services.interactor

import com.droptechsolution.shared.network.NetworkError
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.services.models.OutletService
import com.droptechsolution.shared.services.models.RoomRequest
import com.droptechsolution.shared.services.models.ServicesRequest
import com.droptechsolution.shared.services.models.toDomainList
import com.droptechsolution.shared.services.services.IServicesAPI

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
