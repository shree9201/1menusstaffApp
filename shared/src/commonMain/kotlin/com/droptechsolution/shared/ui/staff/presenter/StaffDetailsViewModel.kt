package com.droptechsolution.shared.ui.staff.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droptechsolution.shared.network.NetworkError
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.ui.common.GENERIC_ERROR_MESSAGE
import com.droptechsolution.shared.ui.common.user.UserStorage
import com.droptechsolution.shared.ui.staff.StaffListRefreshNotifier
import com.droptechsolution.shared.ui.staff.interactor.StaffInteractor
import com.droptechsolution.shared.ui.staff.models.StaffDetailsUi
import com.droptechsolution.shared.ui.staff.models.toStaffDetailsUi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class StaffDetailsViewModel(
    private val staffInteractor: StaffInteractor,
    private val userStorage: UserStorage,
    private val staffListRefreshNotifier: StaffListRefreshNotifier,
) : ViewModel() {

    private val _details = MutableStateFlow<StaffDetailsUi?>(null)
    val details = _details.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting = _isDeleting.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _toastEvents = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toastEvents = _toastEvents.asSharedFlow()

    private val _deletedEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val deletedEvents = _deletedEvents.asSharedFlow()

    private var currentStaffId: String = ""

    init {
        viewModelScope.launch {
            staffListRefreshNotifier.events.collect {
                if (currentStaffId.isNotBlank()) {
                    loadDetails(currentStaffId)
                }
            }
        }
    }

    fun loadDetails(staffId: String) {
        currentStaffId = staffId
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

            when (val result = staffInteractor.loadStaffList(outletId)) {
                is NetworkResult.Success -> {
                    val staff = result.data.firstOrNull { it.id == staffId }
                    if (staff == null) {
                        _details.value = null
                        _errorMessage.value = "Staff details not found"
                    } else {
                        _details.value = staff.toStaffDetailsUi()
                    }
                }
                is NetworkResult.Error -> {
                    _details.value = null
                    _errorMessage.value = result.error.userFacingMessage()
                }
            }
            _isLoading.value = false
        }
    }

    fun deleteStaff() {
        if (currentStaffId.isBlank() || _isDeleting.value) return

        viewModelScope.launch {
            val loggedInStaff = userStorage.getLoggedInStaff().firstOrNull()
            if (loggedInStaff?.id == currentStaffId) {
                _toastEvents.emit("You cannot delete your own account")
                return@launch
            }

            val outletId = userStorage.requireOutletId()
            if (outletId.isBlank()) {
                _toastEvents.emit(GENERIC_ERROR_MESSAGE)
                return@launch
            }

            _isDeleting.value = true
            when (val result = staffInteractor.deleteStaff(outletId, currentStaffId)) {
                is NetworkResult.Success -> {
                    staffListRefreshNotifier.notifyStaffListChanged()
                    _toastEvents.emit(result.data)
                    _deletedEvents.emit(Unit)
                }
                is NetworkResult.Error -> {
                    val message = when (val error = result.error) {
                        is NetworkError.Unknown -> error.userMessage
                        else -> error.userFacingMessage()
                    }
                    _toastEvents.emit(message)
                }
            }
            _isDeleting.value = false
        }
    }
}
