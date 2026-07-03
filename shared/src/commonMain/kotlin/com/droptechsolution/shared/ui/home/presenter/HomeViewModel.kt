package com.droptechsolution.shared.ui.home.presenter

import com.droptechsolution.shared.ui.home.models.DashboardHeaderUi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droptechsolution.shared.ui.common.user.UserStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel (private val userStorage: UserStorage): ViewModel() {

    private val _loginState = MutableStateFlow(DashboardHeaderUi(""))
    val loginState = _loginState.asStateFlow()

    fun checkUserInfo(){
        viewModelScope.launch {
            userStorage.getLoggedInStaff().collect { staffDetails ->
                if (staffDetails != null) {
                    // Staff is logged in! Update state or trigger navigation
//                _staffState.value = staffDetails
                    println("Staff found: ${staffDetails.name}")
                    val user = DashboardHeaderUi(staffDetails.name).also {
                        it.initials = staffDetails.name.take(2)
                        it.role = staffDetails.customised_position?:""
                        it.status = staffDetails.status
                    }
                    _loginState.emit(user)
                } else {
                    // No staff logged in
//                _staffState.value = null
                    println("No staff logged in")
                }
            }
        }


    }
}