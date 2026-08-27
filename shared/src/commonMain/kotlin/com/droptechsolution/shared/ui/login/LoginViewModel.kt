package com.droptechsolution.shared.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droptechsolution.shared.masterdata.interactor.MasterDataInteractor
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.outletinfo.model.api.OutletApi
import com.droptechsolution.shared.outletinfo.model.api.StaffLoginResult
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffLoginRequest
import com.droptechsolution.shared.platform
import com.droptechsolution.shared.push.OneSignalWrapper
import com.droptechsolution.shared.ui.common.user.UserStorage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val pushTokenProvider: IPushTokenProvider,
    private val userStorage: UserStorage,
    private val outletApi: OutletApi,
    private val masterDataInteractor: MasterDataInteractor,
) : ViewModel() {

    private val _loginState = MutableStateFlow(false)
    val loginState = _loginState.asStateFlow()

    private val _toastEvents = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toastEvents = _toastEvents.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isLoggedInState = MutableStateFlow(false)
    val _isLoggedIn = _isLoggedInState.asStateFlow()

    fun login(outletCode: String, username: String, password: String, userType: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val playerId = pushTokenProvider.requestToken().orEmpty()

            when (
                val result = outletApi.staffLogin(
                    StaffLoginRequest(
                        outletId = outletCode,
                        username = username,
                        password = password,
                        deviceId = playerId,
                        deviceType = platform().lowercase(),
                        userType = userType,
                    )
                )
            ) {
                is NetworkResult.Success -> {
                    when (val loginResult = result.data) {
                        is StaffLoginResult.Success -> {
                            userStorage.saveStaffUser(loginResult.staff)
                            userStorage.saveLoginUserType(userType)
                            OneSignalWrapper.setExternalId(loginResult.staff.id)
                            loadMasterData()
                            _loginState.value = true
                        }
                        is StaffLoginResult.Failure -> {
                            _toastEvents.emit(loginResult.message)
                        }
                    }
                }
                is NetworkResult.Error -> {
                    _toastEvents.emit(result.error.userFacingMessage())
                }
            }
            _isLoading.value = false
        }
    }

    fun checkUserInfo() {
        viewModelScope.launch {
            userStorage.getLoggedInStaff().collect { staffDetails ->
                if (staffDetails != null) {
                    _isLoggedInState.emit(true)
                    loadMasterData()
                }
            }
        }
    }

    private suspend fun loadMasterData() {
        when (val result = masterDataInteractor.loadMasterData()) {
            is NetworkResult.Success -> Unit
            is NetworkResult.Error -> {
                // Non-blocking: login still succeeds if master data fails.
            }
        }
    }
}
