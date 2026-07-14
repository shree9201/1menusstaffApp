package com.droptechsolution.shared.ui.staff.interactor

import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffAPI
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffDetails
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffListRequest

class StaffInteractor(
    private val staffAPI: StaffAPI,
) {
    suspend fun loadStaffList(outletId: String): NetworkResult<List<StaffDetails>> =
        when (val result = staffAPI.staffList(StaffListRequest(outletId))) {
            is NetworkResult.Success -> NetworkResult.Success(result.data.value)
            is NetworkResult.Error -> result
        }
}
