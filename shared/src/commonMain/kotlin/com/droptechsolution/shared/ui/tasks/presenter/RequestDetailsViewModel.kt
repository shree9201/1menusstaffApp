package com.droptechsolution.shared.ui.tasks.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.services.interactor.ServicesInteractor
import com.droptechsolution.shared.services.models.RequestAction
import com.droptechsolution.shared.services.models.RequestDetailsUi
import com.droptechsolution.shared.services.models.RequestSource
import com.droptechsolution.shared.services.sla.ServiceSlaProgressUi
import com.droptechsolution.shared.services.sla.ServiceSlaTracker
import com.droptechsolution.shared.ui.common.user.UserStorage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class RequestDetailsViewModel(
    private val servicesInteractor: ServicesInteractor,
    private val userStorage: UserStorage,
    private val slaTracker: ServiceSlaTracker,
) : ViewModel() {

    private val _details = MutableStateFlow<RequestDetailsUi?>(null)
    val details = _details.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating = _isUpdating.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _slaProgress = MutableStateFlow<ServiceSlaProgressUi?>(null)
    val slaProgress = _slaProgress.asStateFlow()

    private var currentRequestId: String = ""
    private var currentSource: RequestSource = RequestSource.ROOM
    private var slaTickerJob: Job? = null

    fun loadDetails(requestId: String, source: RequestSource) {
        currentRequestId = requestId
        currentSource = source
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val outletId = userStorage.requireOutletId()
            if (outletId.isBlank()) {
                _details.value = null
                _errorMessage.value = "Outlet ID not found. Please log in again."
                _isLoading.value = false
                return@launch
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
                    _errorMessage.value = result.error.userMessage
                }
            }
            _isLoading.value = false
        }
    }

    fun performAction(action: RequestAction) {
        if (currentSource != RequestSource.ROOM || currentRequestId.isBlank()) return

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

            when (
                val result = servicesInteractor.updateRequest(
                    outletId = outletId,
                    requestId = currentRequestId,
                    action = action,
                    user = staff,
                )
            ) {
                is NetworkResult.Success -> {
                    applyDetails(result.data)
                    when (action) {
                        RequestAction.START -> startSlaTracking(result.data)
                        RequestAction.COMPLETE,
                        RequestAction.PAUSE,
                        RequestAction.REJECT,
                        RequestAction.HOLD_ESCALATE,
                        -> stopSlaTracking(result.data.id)
                        RequestAction.ACCEPT,
                        RequestAction.PASS,
                        -> Unit
                    }
                }
                is NetworkResult.Error -> {
                    _errorMessage.value = result.error.userMessage
                }
            }
            _isUpdating.value = false
        }
    }

    private suspend fun applyDetails(details: RequestDetailsUi) {
        _details.value = details
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

    override fun onCleared() {
        stopSlaTicker()
        super.onCleared()
    }
}
