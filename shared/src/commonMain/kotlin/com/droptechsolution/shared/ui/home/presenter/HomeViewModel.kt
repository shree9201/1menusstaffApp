package com.droptechsolution.shared.ui.home.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droptechsolution.shared.masterdata.interactor.MasterDataInteractor
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.services.interactor.ServicesInteractor
import com.droptechsolution.shared.services.models.DepartmentOverviewStatUi
import com.droptechsolution.shared.services.models.ServiceRequestRowUi
import com.droptechsolution.shared.ui.common.user.UserStorage
import com.droptechsolution.shared.ui.common.user.toStaffType
import com.droptechsolution.shared.ui.home.models.DashboardHeaderUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userStorage: UserStorage,
    private val serviceInteractor: ServicesInteractor,
    private val masterDataInteractor: MasterDataInteractor,
) : ViewModel() {

    private val _loginState = MutableStateFlow(DashboardHeaderUi(""))
    val loginState = _loginState.asStateFlow()

    private val _activeTasks = MutableStateFlow<List<ServiceRequestRowUi>>(emptyList())
    val activeTasks = _activeTasks.asStateFlow()

    private val _activeTaskCount = MutableStateFlow(0)
    val activeTaskCount = _activeTaskCount.asStateFlow()

    private val _overviewStats = MutableStateFlow<List<DepartmentOverviewStatUi>>(emptyList())
    val overviewStats = _overviewStats.asStateFlow()

    private val _isLoadingStats = MutableStateFlow(false)
    val isLoadingStats = _isLoadingStats.asStateFlow()

    private var isDashboardLoading = false

    init {
        viewModelScope.launch {
            userStorage.getLoggedInStaff().collect { staffDetails ->
                if (staffDetails != null) {
                    _loginState.value = DashboardHeaderUi(staffDetails.name).also {
                        it.initials = staffDetails.name.take(2)
                        it.role = staffDetails.customised_position
                            ?.takeIf { it.isNotBlank() }
                            ?: staffDetails.type.toStaffType().defaultLabel()
                        it.status = staffDetails.status
                    }
                }
            }
        }
    }

    fun loadDashboard() {
        viewModelScope.launch {
            if (isDashboardLoading) return@launch
            isDashboardLoading = true
            try {
                loadDashboardData()
            } finally {
                isDashboardLoading = false
            }
        }
    }

    private suspend fun loadDashboardData() {
        if (masterDataInteractor.masterData.value == null) {
            masterDataInteractor.loadMasterData()
        }

        val outletId = userStorage.requireOutletId()
        if (outletId.isBlank()) {
            _activeTaskCount.value = 0
            _activeTasks.value = emptyList()
            _overviewStats.value = emptyList()
            return
        }

        loadStatusStats(outletId)
        loadServices(outletId)
    }

    private suspend fun loadStatusStats(outletId: String) {
        _isLoadingStats.value = true
        when (val result = serviceInteractor.loadServiceStatusCounts(outletId)) {
            is NetworkResult.Success -> _overviewStats.value = result.data
            is NetworkResult.Error -> _overviewStats.value = emptyList()
        }
        _isLoadingStats.value = false
    }

    private suspend fun loadServices(outletId: String) {
        when (val result = serviceInteractor.loadAllTasks(outletId, activeOnly = true)) {
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
