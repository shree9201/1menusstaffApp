package com.droptechsolution.shared.ui.tasks.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.services.interactor.ServicesInteractor
import com.droptechsolution.shared.services.models.RequestAction
import com.droptechsolution.shared.services.models.RequestDetailsUi
import com.droptechsolution.shared.services.models.RequestSource
import com.droptechsolution.shared.ui.common.user.UserStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class RequestDetailsViewModel(
    private val servicesInteractor: ServicesInteractor,
    private val userStorage: UserStorage,
) : ViewModel() {

    private val _details = MutableStateFlow<RequestDetailsUi?>(null)
    val details = _details.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating = _isUpdating.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private var currentRequestId: String = ""
    private var currentSource: RequestSource = RequestSource.ROOM

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
                    source = source,
                )
            ) {
                is NetworkResult.Success -> {
                    _details.value = result.data
                }
                is NetworkResult.Error -> {
                    _details.value = null
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

            val outletId = userStorage.requireOutletId()
            if (outletId.isBlank()) {
                _errorMessage.value = "Outlet ID not found. Please log in again."
                _isUpdating.value = false
                return@launch
            }

            val staff = userStorage.getLoggedInStaff().firstOrNull()
            val staffId = staff?.id.orEmpty()
            val staffName = staff?.name ?: "Staff"

            when (
                val result = servicesInteractor.updateRequest(
                    outletId = outletId,
                    requestId = currentRequestId,
                    action = action,
                    assignedTo = staffId,
                    staffName = staffName,
                )
            ) {
                is NetworkResult.Success -> {
                    _details.value = result.data
                }
                is NetworkResult.Error -> {
                    _errorMessage.value = result.error.userMessage
                }
            }
            _isUpdating.value = false
        }
    }
}
