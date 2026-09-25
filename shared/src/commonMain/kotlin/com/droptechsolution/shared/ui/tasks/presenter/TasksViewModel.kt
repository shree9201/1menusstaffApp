package com.droptechsolution.shared.ui.tasks.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.services.TaskListRefreshNotifier
import com.droptechsolution.shared.services.interactor.ServicesInteractor
import com.droptechsolution.shared.services.models.DepartmentOverviewCategory
import com.droptechsolution.shared.services.models.filterStatuses
import com.droptechsolution.shared.services.models.ServiceRequestRowUi
import com.droptechsolution.shared.services.models.forActor
import com.droptechsolution.shared.ui.common.user.UserSession
import com.droptechsolution.shared.ui.common.user.UserStorage
import com.droptechsolution.shared.ui.common.user.toStaffDepartment
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TasksViewModel(
    private val servicesInteractor: ServicesInteractor,
    private val userStorage: UserStorage,
    private val taskListRefreshNotifier: TaskListRefreshNotifier,
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<ServiceRequestRowUi>>(emptyList())
    val tasks = _tasks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _statusFilter = MutableStateFlow<String?>(null)
    val statusFilter = _statusFilter.asStateFlow()

    private val _overviewCategory = MutableStateFlow<DepartmentOverviewCategory?>(null)
    val overviewCategory = _overviewCategory.asStateFlow()

    private val _userSession = MutableStateFlow<UserSession?>(null)
    val userSession = _userSession.asStateFlow()

    private var loadJob: Job? = null
    private var lastLoadedKey: TasksLoadKey? = null

    init {
        viewModelScope.launch {
            taskListRefreshNotifier.events.collect {
                refreshTasks()
            }
        }
    }

    fun loadTasks(
        statusFilter: String? = null,
        overviewCategoryKey: String? = null,
        userSession: UserSession? = null,
        forceRefresh: Boolean = false,
    ) {
        _userSession.value = userSession
        val loadKey = TasksLoadKey(statusFilter, overviewCategoryKey)
        updateFilterState(statusFilter, overviewCategoryKey)

        if (!forceRefresh && loadKey == lastLoadedKey && _tasks.value.isNotEmpty()) {
            return
        }
        if (loadJob?.isActive == true && loadKey == lastLoadedKey) {
            return
        }

        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val outletId = userStorage.requireOutletId()
            if (outletId.isBlank()) {
                _tasks.value = emptyList()
                _errorMessage.value = "Outlet ID not found. Please log in again."
                _isLoading.value = false
                return@launch
            }

            val staffScope = userStorage.staffRequestScope()
            val statusFilters = _overviewCategory.value?.filterStatuses().orEmpty()

            when (
                val result = servicesInteractor.loadAllTasks(
                    outletId = outletId,
                    activeOnly = false,
                    statusFilter = statusFilter,
                    statusFilters = statusFilters,
                    staffScope = staffScope,
                )
            ) {
                is NetworkResult.Success -> {
                    val department = staffScope.loginUserType.orEmpty().toStaffDepartment()
                    _tasks.value = result.data.rows.map { it.forActor(department) }
                    lastLoadedKey = loadKey
                }
                is NetworkResult.Error -> {
                    _tasks.value = emptyList()
                    _errorMessage.value = result.error.userMessage
                }
            }
            _isLoading.value = false
        }
    }

    fun refreshTasks() {
        loadTasks(
            statusFilter = _statusFilter.value,
            overviewCategoryKey = _overviewCategory.value?.name,
            userSession = _userSession.value,
            forceRefresh = true,
        )
    }

    private fun updateFilterState(
        statusFilter: String?,
        overviewCategoryKey: String?,
    ) {
        _statusFilter.value = statusFilter
        _overviewCategory.value = overviewCategoryKey?.let { key ->
            DepartmentOverviewCategory.entries.firstOrNull { it.name == key }
        }
    }

    private data class TasksLoadKey(
        val statusFilter: String?,
        val overviewCategoryKey: String?,
    )
}
