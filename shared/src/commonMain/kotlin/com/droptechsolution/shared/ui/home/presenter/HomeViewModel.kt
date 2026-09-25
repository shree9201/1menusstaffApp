package com.droptechsolution.shared.ui.home.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droptechsolution.shared.masterdata.interactor.MasterDataInteractor
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.services.TaskListRefreshNotifier
import com.droptechsolution.shared.services.interactor.ServicesInteractor
import com.droptechsolution.shared.services.models.DepartmentOverviewStatUi
import com.droptechsolution.shared.services.models.ServiceRequestRowUi
import com.droptechsolution.shared.services.models.StaffRequestScope
import com.droptechsolution.shared.services.models.forActor
import com.droptechsolution.shared.ui.common.user.UserStorage
import com.droptechsolution.shared.ui.common.user.toInitials
import com.droptechsolution.shared.ui.common.user.toStaffDepartment
import com.droptechsolution.shared.ui.common.user.toStaffType
import com.droptechsolution.shared.ui.home.models.DashboardHeaderUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userStorage: UserStorage,
    private val serviceInteractor: ServicesInteractor,
    private val masterDataInteractor: MasterDataInteractor,
    private val taskListRefreshNotifier: TaskListRefreshNotifier,
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
                        it.initials = staffDetails.name.toInitials()
                        it.role = staffDetails.customised_position
                            ?.takeIf { it.isNotBlank() }
                            ?: staffDetails.type.toStaffType().defaultLabel()
                        it.status = staffDetails.status
                    }
                }
            }
        }
        viewModelScope.launch {
            taskListRefreshNotifier.events.collect {
                loadDashboard(forceRefresh = true)
            }
        }
    }

    fun loadDashboard(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (!forceRefresh && isDashboardLoading) return@launch
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

        loadStatusStats(outletId, userStorage.staffRequestScope())
        loadServices(outletId, userStorage.staffRequestScope())
    }

    private suspend fun loadStatusStats(
        outletId: String,
        staffScope: StaffRequestScope,
    ) {
        _isLoadingStats.value = true
        when (val result = serviceInteractor.loadServiceStatusCounts(outletId, staffScope)) {
            is NetworkResult.Success -> _overviewStats.value = result.data
            is NetworkResult.Error -> _overviewStats.value = emptyList()
        }
        _isLoadingStats.value = false
    }

    private suspend fun loadServices(
        outletId: String,
        staffScope: StaffRequestScope,
    ) {
        when (
            val result = serviceInteractor.loadAllTasks(
                outletId = outletId,
                activeOnly = true,
                staffScope = staffScope,
                limit = HOME_ROOM_REQUESTS_LIMIT,
            )
        ) {
            is NetworkResult.Success -> {
                val department = staffScope.loginUserType.orEmpty().toStaffDepartment()
                val rows = result.data.rows.map { it.forActor(department) }
                _activeTaskCount.value = rows.size
                _activeTasks.value = rows
            }
            is NetworkResult.Error -> {
                _activeTaskCount.value = 0
                _activeTasks.value = emptyList()
            }
        }
    }

    private companion object {
        const val HOME_ROOM_REQUESTS_LIMIT = "0,10"
    }
}
