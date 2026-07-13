package com.droptechsolution.shared.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droptechsolution.shared.masterdata.interactor.MasterDataInteractor
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.outletinfo.model.api.OutletApi
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffLoginRequest
import com.droptechsolution.shared.platform
import com.droptechsolution.shared.ui.common.user.UserStorage
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val _errorMessage = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isLoggedInState = MutableStateFlow(false)
    val _isLoggedIn = _isLoggedInState.asStateFlow()

    fun login(outletCode: String, username: String, password: String, userType: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            val pushToken = pushTokenProvider.requestToken().orEmpty()

            when (
                val result = outletApi.staffLogin(
                    StaffLoginRequest(
                        outletId = outletCode,
                        username = username,
                        password = password,
                        deviceId = pushToken,
                        deviceType = platform().lowercase(),
                        userType = userType,
                    )
                )
            ) {
                is NetworkResult.Success -> {
                    val response = result.data
                    if (!response.status) {
                        _errorMessage.value = "Invalid credentials"
                    } else {
                        userStorage.saveStaffUser(response.value)
                        loadMasterData()
                        _loginState.value = true
                    }
                }
                is NetworkResult.Error -> {
                    _errorMessage.value = result.error.userMessage
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
