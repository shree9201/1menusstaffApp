package com.droptechsolution.shared.ui.tasks.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droptechsolution.shared.network.NetworkError
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.services.TaskListRefreshNotifier
import com.droptechsolution.shared.services.interactor.ServicesInteractor
import com.droptechsolution.shared.services.models.RequestAction
import com.droptechsolution.shared.services.models.apiStatus
import com.droptechsolution.shared.services.models.toAvailableActions
import com.droptechsolution.shared.services.models.toStatusDisplay
import com.droptechsolution.shared.services.models.RequestDetailsUi
import com.droptechsolution.shared.services.models.RequestSource
import com.droptechsolution.shared.services.sla.ServiceSlaProgressUi
import com.droptechsolution.shared.services.sla.ServiceSlaTracker
import com.droptechsolution.shared.ui.common.GENERIC_ERROR_MESSAGE
import com.droptechsolution.shared.ui.common.user.StaffDepartment
import com.droptechsolution.shared.ui.common.user.UserStorage
import com.droptechsolution.shared.ui.common.user.toStaffDepartment
import com.droptechsolution.shared.ui.staff.interactor.StaffInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class AssignStaffOption(
    val id: String,
    val name: String,
)

class RequestDetailsViewModel(
    private val servicesInteractor: ServicesInteractor,
    private val userStorage: UserStorage,
    private val slaTracker: ServiceSlaTracker,
    private val taskListRefreshNotifier: TaskListRefreshNotifier,
    private val staffInteractor: StaffInteractor,
) : ViewModel() {

    private val _details = MutableStateFlow<RequestDetailsUi?>(null)
    val details = _details.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating = _isUpdating.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _toastEvents = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toastEvents = _toastEvents.asSharedFlow()

    private val _slaProgress = MutableStateFlow<ServiceSlaProgressUi?>(null)
    val slaProgress = _slaProgress.asStateFlow()

    private val _assignStaffOptions = MutableStateFlow<List<AssignStaffOption>>(emptyList())
    val assignStaffOptions = _assignStaffOptions.asStateFlow()

    private var currentRequestId: String = ""
    private var currentSource: RequestSource = RequestSource.ROOM
    private var actorDepartment: StaffDepartment = StaffDepartment.STAFF
    private var slaTickerJob: Job? = null

    fun loadDetails(requestId: String, source: RequestSource) {
        currentRequestId = requestId
        currentSource = source
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            actorDepartment = resolveActorDepartment()

            val outletId = userStorage.requireOutletId()
            if (outletId.isBlank()) {
                _details.value = null
                _errorMessage.value = "Outlet ID not found. Please log in again."
                _isLoading.value = false
                return@launch
            }

            if (actorDepartment.canAssignRequests()) {
                loadAssignableStaff(outletId)
            } else {
                _assignStaffOptions.value = emptyList()
            }

            when (
                val result = servicesInteractor.loadRequestDetails(
                    outletId = outletId,
                    requestId = requestId,
                )
            ) {
                is NetworkResult.Success -> {
                    applyDetails(result.data)
                }
                is NetworkResult.Error -> {
                    _details.value = null
                    _slaProgress.value = null
                    stopSlaTicker()
                    showError(result.error)
                }
            }
            _isLoading.value = false
        }
    }

    fun performAction(action: RequestAction, assignedToId: String? = null) {
        if (currentSource != RequestSource.ROOM || currentRequestId.isBlank()) return
        if (action == RequestAction.ASSIGN) {
            val staffId = assignedToId?.takeIf { it.isNotBlank() }
            if (staffId == null) {
                viewModelScope.launch { _toastEvents.emit("Please select a staff member") }
                return
            }
            submitAction(action, assignedToId = staffId)
            return
        }
        submitAction(action)
    }

    private fun submitAction(
        action: RequestAction,
        assignedToId: String? = null,
    ) {
        viewModelScope.launch {
            _isUpdating.value = true
            _errorMessage.value = null

            val staff = userStorage.getLoggedInStaff().firstOrNull()
            if (staff == null) {
                _errorMessage.value = "User not found. Please log in again."
                _isUpdating.value = false
                return@launch
            }

            val outletId = staff.userId.ifBlank { userStorage.requireOutletId() }
            if (outletId.isBlank()) {
                _errorMessage.value = "Outlet ID not found. Please log in again."
                _isUpdating.value = false
                return@launch
            }

            val assignee = assignedToId?.let { id ->
                _assignStaffOptions.value.firstOrNull { it.id == id }
            }

            when (
                val result = servicesInteractor.updateRequest(
                    outletId = outletId,
                    requestId = currentRequestId,
                    action = action,
                    user = staff,
                    assignedTo = assignedToId ?: staff.id,
                    assigneeName = assignee?.name ?: staff.name,
                )
            ) {
                is NetworkResult.Success -> {
                    _toastEvents.emit(result.data.message)
                    val details = result.data.details
                    if (details != null) {
                        applyDetails(details)
                        when (action) {
                            RequestAction.START -> startSlaTracking(details)
                            RequestAction.COMPLETE,
                            RequestAction.PAUSE,
                            RequestAction.REJECT,
                            RequestAction.HOLD_ESCALATE,
                            -> stopSlaTracking(details.id)
                            RequestAction.ACCEPT,
                            RequestAction.ASSIGN,
                            RequestAction.PASS,
                            -> Unit
                        }
                    } else {
                        applyLocalActionResult(action)
                    }
                    taskListRefreshNotifier.notifyTaskListChanged()
                }
                is NetworkResult.Error -> showActionError(result.error)
            }
            _isUpdating.value = false
        }
    }

    private suspend fun applyLocalActionResult(action: RequestAction) {
        val current = _details.value ?: return
        val updatedStatus = action.apiStatus()
        stopSlaTracking(current.id)
        _details.value = current.copy(
            status = updatedStatus,
            statusDisplay = updatedStatus.toStatusDisplay(),
            availableActions = updatedStatus.toAvailableActions(actorDepartment),
        )
    }

    private suspend fun applyDetails(details: RequestDetailsUi) {
        _details.value = details.copy(
            availableActions = details.status.toAvailableActions(actorDepartment),
        )
        if (details.reminderTimeMinutes <= 0) {
            _slaProgress.value = null
            stopSlaTicker()
            return
        }

        slaTracker.ensureSessionForInProgressRequest(
            requestId = details.id,
            reminderMinutes = details.reminderTimeMinutes,
            workStartedAt = details.workStartedAt,
            statusDisplay = details.statusDisplay,
        )
        refreshSlaProgress(details)
        startSlaTickerIfNeeded(details)
    }

    private suspend fun startSlaTracking(details: RequestDetailsUi) {
        if (details.reminderTimeMinutes <= 0) return
        slaTracker.start(details.id, details.reminderTimeMinutes)
        refreshSlaProgress(details)
        startSlaTickerIfNeeded(details)
    }

    private suspend fun stopSlaTracking(requestId: String) {
        slaTracker.stop(requestId)
        stopSlaTicker()
        _details.value?.let { refreshSlaProgress(it) }
    }

    private fun startSlaTickerIfNeeded(details: RequestDetailsUi) {
        if (details.reminderTimeMinutes <= 0) {
            stopSlaTicker()
            return
        }
        slaTickerJob?.cancel()
        slaTickerJob = viewModelScope.launch {
            refreshSlaProgress(details)
            while (isActive) {
                if (_slaProgress.value?.isActive != true) break
                delay(1_000)
                val currentDetails = _details.value ?: break
                refreshSlaProgress(currentDetails)
            }
        }
    }

    private fun stopSlaTicker() {
        slaTickerJob?.cancel()
        slaTickerJob = null
    }

    private suspend fun refreshSlaProgress(details: RequestDetailsUi) {
        if (details.reminderTimeMinutes <= 0) {
            _slaProgress.value = null
            return
        }
        _slaProgress.value = slaTracker.getProgressForSession(
            requestId = details.id,
            reminderMinutes = details.reminderTimeMinutes,
            title = details.slaCardTitle,
            priorityLabel = details.priorityShort,
            workStartedAt = details.workStartedAt,
            statusDisplay = details.statusDisplay,
        )
    }

    private suspend fun resolveActorDepartment(): StaffDepartment {
        val loginType = userStorage.getLoginUserType().firstOrNull().orEmpty().toStaffDepartment()
        if (loginType != StaffDepartment.UNKNOWN) return loginType
        return userStorage.getLoggedInStaff().firstOrNull()
            ?.department
            .orEmpty()
            .toStaffDepartment()
    }

    private suspend fun loadAssignableStaff(outletId: String) {
        val loggedInId = userStorage.getLoggedInStaff().firstOrNull()?.id
        when (val result = staffInteractor.loadStaffList(outletId)) {
            is NetworkResult.Success -> {
                val staffMembers = result.data.filter { it.id != loggedInId }
                val operationalStaff = staffMembers.filter {
                    it.department.orEmpty().equals("STAFF", ignoreCase = true)
                }
                val options = (operationalStaff.ifEmpty { staffMembers }).map {
                    AssignStaffOption(id = it.id, name = it.name)
                }
                _assignStaffOptions.value = options
            }
            is NetworkResult.Error -> _assignStaffOptions.value = emptyList()
        }
    }

    private fun showError(error: NetworkError) {
        _errorMessage.value = error.userFacingMessage()
    }

    private suspend fun showActionError(error: NetworkError) {
        _toastEvents.emit(error.userFacingMessage())
    }

    override fun onCleared() {
        stopSlaTicker()
        super.onCleared()
    }
}
