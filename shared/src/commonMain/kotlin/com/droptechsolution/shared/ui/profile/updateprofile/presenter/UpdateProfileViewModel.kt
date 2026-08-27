package com.droptechsolution.shared.ui.profile.updateprofile.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droptechsolution.shared.network.NetworkError
import com.droptechsolution.shared.network.NetworkLogger
import com.droptechsolution.shared.network.NETWORK_LOG_TAG
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffDetails
import com.droptechsolution.shared.ui.common.GENERIC_ERROR_MESSAGE
import com.droptechsolution.shared.ui.common.user.UserStorage
import com.droptechsolution.shared.ui.profile.updateprofile.models.UpdateProfileUiState
import com.droptechsolution.shared.ui.staff.interactor.StaffInteractor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UpdateProfileViewModel(
    private val staffInteractor: StaffInteractor,
    private val userStorage: UserStorage,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdateProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val _toastEvents = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toastEvents = _toastEvents.asSharedFlow()

    private val _successEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val successEvents = _successEvents.asSharedFlow()

    private var currentStaff: StaffDetails? = null

    fun loadProfile() {
        viewModelScope.launch {
            val staff = userStorage.getLoggedInStaff().firstOrNull()
            currentStaff = staff
            if (staff == null) {
                NetworkLogger.e(
                    tag = NETWORK_LOG_TAG,
                    message = "updateProfile load aborted: staff session not found",
                )
                _toastEvents.emit(GENERIC_ERROR_MESSAGE)
                return@launch
            }

            _uiState.update {
                UpdateProfileUiState(
                    name = staff.name,
                    email = staff.email.orEmpty(),
                    mobile = staff.mobile.orEmpty(),
                    username = staff.username,
                    customisedPosition = staff.customised_position.orEmpty(),
                    isLoaded = true,
                )
            }
        }
    }

    fun onNameChanged(value: String) = updateField { it.copy(name = value) }

    fun onEmailChanged(value: String) = updateField { it.copy(email = value) }

    fun onMobileChanged(value: String) = updateField { it.copy(mobile = value) }

    fun onUsernameChanged(value: String) = updateField { it.copy(username = value) }

    fun onCustomisedPositionChanged(value: String) = updateField { it.copy(customisedPosition = value) }

    fun saveProfile() {
        val state = _uiState.value
        val validationMessage = validate(state)
        if (validationMessage != null) {
            viewModelScope.launch { _toastEvents.emit(validationMessage) }
            return
        }

        val staff = currentStaff
        val outletId = staff?.userId.orEmpty()
        val staffId = staff?.id.orEmpty()
        if (staff == null || outletId.isBlank() || staffId.isBlank()) {
            viewModelScope.launch {
                NetworkLogger.e(
                    tag = NETWORK_LOG_TAG,
                    message = "updateProfile save aborted: staff session not found",
                )
                _toastEvents.emit(GENERIC_ERROR_MESSAGE)
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (
                val result = staffInteractor.updateProfile(
                    outletId = outletId,
                    staffId = staffId,
                    name = state.name.trim(),
                    email = state.email.trim(),
                    mobile = state.mobile.trim(),
                    username = state.username.trim(),
                    customisedPosition = state.customisedPosition.trim(),
                )
            ) {
                is NetworkResult.Success -> {
                    userStorage.saveStaffUser(
                        staff.copy(
                            name = state.name.trim(),
                            email = state.email.trim(),
                            mobile = state.mobile.trim(),
                            username = state.username.trim(),
                            customised_position = state.customisedPosition.trim(),
                        ),
                    )
                    _toastEvents.emit(result.data)
                    delay(800)
                    _successEvents.emit(Unit)
                }
                is NetworkResult.Error -> {
                    NetworkLogger.e(
                        tag = NETWORK_LOG_TAG,
                        message = "updateProfile failed: ${result.error.userMessage}",
                        throwable = (result.error as? NetworkError.Serialization)?.cause,
                    )
                    val message = when (result.error) {
                        is NetworkError.Unknown -> result.error.userMessage
                        else -> result.error.userFacingMessage()
                    }
                    _toastEvents.emit(message)
                }
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun updateField(transform: (UpdateProfileUiState) -> UpdateProfileUiState) {
        _uiState.update(transform)
    }

    private fun validate(state: UpdateProfileUiState): String? = when {
        state.name.isBlank() -> "Name is required"
        state.email.isBlank() -> "Email is required"
        state.mobile.isBlank() -> "Mobile number is required"
        state.username.isBlank() -> "Username is required"
        state.customisedPosition.isBlank() -> "Position is required"
        else -> null
    }
}
