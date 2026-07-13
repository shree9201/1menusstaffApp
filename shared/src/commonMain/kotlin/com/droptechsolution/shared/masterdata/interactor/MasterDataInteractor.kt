package com.droptechsolution.shared.masterdata.interactor

import com.droptechsolution.shared.masterdata.api.IMasterDataApi
import com.droptechsolution.shared.masterdata.models.MasterData
import com.droptechsolution.shared.masterdata.models.isSuccessful
import com.droptechsolution.shared.masterdata.models.toDomain
import com.droptechsolution.shared.network.NetworkError
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffDetails
import com.droptechsolution.shared.ui.common.user.UserStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MasterDataInteractor(
    private val masterDataApi: IMasterDataApi,
    private val userStorage: UserStorage,
) {
    private val _masterData = MutableStateFlow<MasterData?>(null)
    val masterData: StateFlow<MasterData?> = _masterData.asStateFlow()

    suspend fun loadMasterData(): NetworkResult<MasterData> =
        when (val result = masterDataApi.getMasterData()) {
            is NetworkResult.Success -> {
                if (result.data.isSuccessful()) {
                    val data = result.data.toDomain()
                    _masterData.value = data
                    data.user?.let { userStorage.saveStaffUser(it) }
                    NetworkResult.Success(data)
                } else {
                    NetworkResult.Error(NetworkError.Unknown("Unable to load master data"))
                }
            }
            is NetworkResult.Error -> result
        }

    fun currentUser(): StaffDetails? =
        _masterData.value?.user
}
