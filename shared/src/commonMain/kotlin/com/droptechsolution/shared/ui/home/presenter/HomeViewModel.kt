package com.droptechsolution.shared.ui.home.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.services.interactor.ServicesInteractor
import com.droptechsolution.shared.services.models.ServiceRequestRowUi
import com.droptechsolution.shared.ui.common.user.UserStorage
import com.droptechsolution.shared.ui.home.models.DashboardHeaderUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userStorage: UserStorage,
    private val serviceInteractor: ServicesInteractor,
) : ViewModel() {

    private val _loginState = MutableStateFlow(DashboardHeaderUi(""))
    val loginState = _loginState.asStateFlow()

    private val _activeTasks = MutableStateFlow<List<ServiceRequestRowUi>>(emptyList())
    val activeTasks = _activeTasks.asStateFlow()

    private val _activeTaskCount = MutableStateFlow(0)
    val activeTaskCount = _activeTaskCount.asStateFlow()

    var outletID = "5"

    fun checkUserInfo() {
        viewModelScope.launch {
            userStorage.getLoggedInStaff().collect { staffDetails ->
                if (staffDetails != null) {
                    val user = DashboardHeaderUi(staffDetails.name).also {
                        it.initials = staffDetails.name.take(2)
                        it.role = staffDetails.customised_position ?: ""
                        it.status = staffDetails.status
                    }
                    _loginState.emit(user)
                }
            }
        }
        viewModelScope.launch {
            loadServices()
        }
    }

    fun loadServices() {
        viewModelScope.launch {
            when (val result = serviceInteractor.loadAllTasks(outletID, activeOnly = true)) {
                is NetworkResult.Success -> {
                    _activeTaskCount.value = result.data.rows.size
                    _activeTasks.value = result.data.rows
                }
                is NetworkResult.Error -> {
                    _activeTaskCount.value = 0
                    _activeTasks.value = emptyList()
                }
            }
        }
    }
}
