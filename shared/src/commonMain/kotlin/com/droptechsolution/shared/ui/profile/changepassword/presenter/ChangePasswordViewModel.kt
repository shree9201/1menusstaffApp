package com.droptechsolution.shared.ui.profile.changepassword.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droptechsolution.shared.network.NetworkError
import com.droptechsolution.shared.network.NetworkLogger
import com.droptechsolution.shared.network.NETWORK_LOG_TAG
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.ui.common.GENERIC_ERROR_MESSAGE
import com.droptechsolution.shared.ui.common.user.UserStorage
import com.droptechsolution.shared.ui.staff.interactor.StaffInteractor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ChangePasswordViewModel(
    private val staffInteractor: StaffInteractor,
    private val userStorage: UserStorage,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _toastEvents = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toastEvents = _toastEvents.asSharedFlow()

    private val _successEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val successEvents = _successEvents.asSharedFlow()

    fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String,
    ) {
        val validationMessage = validate(currentPassword, newPassword, confirmPassword)
        if (validationMessage != null) {
            viewModelScope.launch {
                _toastEvents.emit(validationMessage)
            }
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            val staff = userStorage.getLoggedInStaff().firstOrNull()
            val outletId = staff?.userId.orEmpty()
            val staffId = staff?.id.orEmpty()
            if (outletId.isBlank() || staffId.isBlank()) {
                NetworkLogger.e(
                    tag = NETWORK_LOG_TAG,
                    message = "changePassword aborted: staff session not found",
                )
                _toastEvents.emit(GENERIC_ERROR_MESSAGE)
                _isLoading.value = false
                return@launch
            }

            when (
                val result = staffInteractor.changePassword(
                    outletId = outletId,
                    staffId = staffId,
                    oldPassword = currentPassword,
                    newPassword = newPassword,
                )
            ) {
                is NetworkResult.Success -> {
                    _toastEvents.emit(result.data)
                    delay(800)
                    _successEvents.emit(Unit)
                }
                is NetworkResult.Error -> {
                    NetworkLogger.e(
                        tag = NETWORK_LOG_TAG,
                        message = "changePassword failed: ${result.error.userMessage}",
                        throwable = (result.error as? NetworkError.Serialization)?.cause,
                    )
                    val message = when (result.error) {
                        is NetworkError.Unknown -> result.error.userMessage
                        else -> result.error.userFacingMessage()
                    }
                    _toastEvents.emit(message)
                }
            }
            _isLoading.value = false
        }
    }

    private fun validate(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String,
    ): String? = when {
        currentPassword.isBlank() -> "Current password is required"
        newPassword.isBlank() -> "New password is required"
        confirmPassword.isBlank() -> "Confirm new password is required"
        newPassword != confirmPassword -> "New passwords do not match"
        newPassword == currentPassword -> "New password must be different from current password"
        else -> null
    }
}
