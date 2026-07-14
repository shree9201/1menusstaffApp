package com.droptechsolution.menus.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.droptechsolution.menus.push.ITokenService
import com.droptechsolution.shared.network.NetworkProvider
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.outletinfo.model.api.OutletRequest
import com.droptechsolution.shared.outletinfo.model.api.staff.NotificationRequest
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffDetails
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffListRequest
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffLoginRequest
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val tokenService: ITokenService) : ViewModel() {
    private val outletApi = NetworkProvider.outletApi
    private val staffApi = NetworkProvider.staffApi
//    private val _loginState = MutableStateFlow(Boolean)
//    val loginState = _loginState.asStateFlow()

    private val _loginState = MutableLiveData<Boolean>()
    val loginState : LiveData<Boolean> = _loginState

    private val _staffs = MutableStateFlow<List<StaffDetails>>(emptyList())
    val staffs = _staffs.asStateFlow()

    fun loadInfo(username:String, password:String){
        viewModelScope.launch {
            // loadOutletInfo(username,password)
            loadToken(username,password)
        }
    }

    suspend fun loadOutletInfo(username:String, password:String){
        when (val result = outletApi.getOutletInfo(OutletRequest(username, password))) {
            is NetworkResult.Success -> Log.v("HomeViewModel", "${result.data}")
            is NetworkResult.Error -> Log.e("HomeViewModel", result.error.userMessage)
        }
    }

    fun loadToken(username:String,password:String){
        viewModelScope.launch {

            val pushToken = tokenService.requestToken()
            Log.v("HomeViewModel", "Token : $pushToken")

//            val info = OutletApi().staffLogin(StaffLoginRequest(outletId = "5", username = "cafe99mgr", password = "cafe99mgr", deviceId = pushToken?:"", deviceType = "android"))
            val info = outletApi.staffLogin(
                StaffLoginRequest(
                    outletId = "5",
                    username = username,
                    password = password,
                    deviceId = pushToken ?: "",
                    deviceType = "android",
                    userType = "STAFF",
                )
            )
            when (info) {
                is NetworkResult.Success -> {
                    Log.v("HomeViewModel", "${info.data}")
                    if (info.data.status) {
                        _loginState.postValue(true)
                    }
                }
                is NetworkResult.Error -> Log.e("HomeViewModel", info.error.userMessage)
            }

        }

    }

    fun loadStaffs(){
        viewModelScope.launch {
            when (val staff = staffApi.staffList(StaffListRequest("5"))) {
                is NetworkResult.Success -> {
                    Log.v("HomeViewModel", "$staff")
                    _staffs.emit(staff.data.value)
                }
                is NetworkResult.Error -> Log.e("HomeViewModel", staff.error.userMessage)
            }
        }
    }

    fun sendNotification(staffId: String, titleText: String, messageText: String){
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            Log.e("HomeViewModel", "Error sending notification: ${throwable.message}")
            throwable.printStackTrace()
        }
        val scope = CoroutineScope(Dispatchers.IO + exceptionHandler)

        scope.launch {
            when (val result = outletApi.sendNotification(NotificationRequest(staffId, titleText, messageText))) {
                is NetworkResult.Success -> Log.v("HomeViewModel", result.data)
                is NetworkResult.Error -> Log.e("HomeViewModel", result.error.userMessage)
            }
        }
    }

}


class HomeViewModelFactory(val tokenService : ITokenService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(tokenService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}