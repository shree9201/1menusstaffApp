package com.droptechsolution.shared.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
) : ViewModel() {

    private val _loginState = MutableStateFlow(false)
    val loginState = _loginState.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    private val _isLoggedInState = MutableStateFlow(false)
    val _isLoggedIn = _isLoggedInState.asStateFlow()

    fun login(outletCode: String, username: String, password: String,userType:String) {
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
                        userStorage.saveOutletId(outletCode)
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

    fun checkUserInfo(){
        viewModelScope.launch {
            userStorage.getLoggedInStaff().collect { staffDetails ->
                if (staffDetails != null) {
                    // Staff is logged in! Update state or trigger navigation
//                _staffState.value = staffDetails
                    println("Staff found: ${staffDetails.name}")
                    _isLoggedInState.emit(true)
                } else {
                    // No staff logged in
//                _staffState.value = null
                    println("No staff logged in")
                }
            }
        }


    }

}
//
//val loginViewModelFactory = viewModelFactory {
//    initializer {
//        LoginViewModel()
//    }
//}
