package com.droptechsolution.shared.ui.staff.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droptechsolution.shared.masterdata.interactor.MasterDataInteractor
import com.droptechsolution.shared.network.NetworkError
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.ui.common.GENERIC_ERROR_MESSAGE
import com.droptechsolution.shared.ui.common.user.UserStorage
import com.droptechsolution.shared.ui.staff.StaffListRefreshNotifier
import com.droptechsolution.shared.ui.staff.interactor.StaffInteractor
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StaffFormUiState(
    val isEdit: Boolean = false,
    val name: String = "",
    val email: String = "",
    val mobile: String = "",
    val username: String = "",
    val password: String = "",
    val department: String = "",
    val type: String = "",
    val departmentOptions: List<Pair<String, String>> = emptyList(),
    val typeOptions: List<Pair<String, String>> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
)

class StaffFormViewModel(
    private val staffInteractor: StaffInteractor,
    private val userStorage: UserStorage,
    private val masterDataInteractor: MasterDataInteractor,
    private val staffListRefreshNotifier: StaffListRefreshNotifier,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StaffFormUiState())
    val uiState = _uiState.asStateFlow()

    private val _toastEvents = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toastEvents = _toastEvents.asSharedFlow()

    private val _successEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val successEvents = _successEvents.asSharedFlow()

    private var editingStaffId: String? = null

    fun load(staffId: String?) {
        editingStaffId = staffId?.takeIf { it.isNotBlank() }
        viewModelScope.launch {
            _uiState.update { it.copy(isEdit = editingStaffId != null, isLoading = true) }

            val masterData = when (val result = masterDataInteractor.loadMasterData()) {
                is NetworkResult.Success -> result.data
                is NetworkResult.Error -> masterDataInteractor.masterData.value
            }
            val departmentOptions = masterData?.departments.orEmpty().map { it.key to it.value }
            val typeOptions = masterData?.staffTypes.orEmpty().map { it.key to it.value }

            if (masterData == null) {
                _toastEvents.emit(GENERIC_ERROR_MESSAGE)
            }

            if (editingStaffId == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        departmentOptions = departmentOptions,
                        typeOptions = typeOptions,
                        department = it.department.ifBlank { departmentOptions.firstOrNull()?.first.orEmpty() },
                        type = it.type.ifBlank { typeOptions.firstOrNull()?.first.orEmpty() },
                    )
                }
                return@launch
            }

            val outletId = userStorage.requireOutletId()
            if (outletId.isBlank()) {
                _uiState.update { it.copy(isLoading = false) }
                _toastEvents.emit(GENERIC_ERROR_MESSAGE)
                return@launch
            }

            when (val result = staffInteractor.loadStaffList(outletId)) {
                is NetworkResult.Success -> {
                    val staff = result.data.firstOrNull { it.id == editingStaffId }
                    if (staff == null) {
                        _uiState.update { it.copy(isLoading = false) }
                        _toastEvents.emit("Staff details not found")
                        return@launch
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            name = staff.name,
                            email = staff.email.orEmpty(),
                            mobile = staff.mobile.orEmpty(),
                            username = staff.username,
                            department = staff.department.orEmpty()
                                .ifBlank { departmentOptions.firstOrNull()?.first.orEmpty() },
                            type = staff.type.ifBlank { typeOptions.firstOrNull()?.first.orEmpty() },
                            departmentOptions = departmentOptions,
                            typeOptions = typeOptions,
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _toastEvents.emit(result.error.userFacingMessage())
                }
            }
        }
    }

    fun onNameChanged(value: String) = _uiState.update { it.copy(name = value) }
    fun onEmailChanged(value: String) = _uiState.update { it.copy(email = value) }
    fun onMobileChanged(value: String) = _uiState.update { it.copy(mobile = value) }
    fun onUsernameChanged(value: String) = _uiState.update { it.copy(username = value) }
    fun onPasswordChanged(value: String) = _uiState.update { it.copy(password = value) }
    fun onDepartmentChanged(value: String) = _uiState.update { it.copy(department = value) }
    fun onTypeChanged(value: String) = _uiState.update { it.copy(type = value) }

    fun save() {
        val state = _uiState.value
        if (state.isSaving) return

        viewModelScope.launch {
            val name = state.name.trim()
            val email = state.email.trim()
            val mobile = state.mobile.trim()
            val username = state.username.trim()
            val password = state.password.trim()
            val department = state.department.trim()
            val type = state.type.trim()

            when {
                name.isBlank() -> {
                    _toastEvents.emit("Please enter full name")
                    return@launch
                }
                username.isBlank() -> {
                    _toastEvents.emit("Please enter username")
                    return@launch
                }
                !state.isEdit && password.isBlank() -> {
                    _toastEvents.emit("Please enter password")
                    return@launch
                }
                department.isBlank() -> {
                    _toastEvents.emit("Please select department")
                    return@launch
                }
                type.isBlank() -> {
                    _toastEvents.emit("Please select type")
                    return@launch
                }
            }

            val outletId = userStorage.requireOutletId()
            if (outletId.isBlank()) {
                _toastEvents.emit(GENERIC_ERROR_MESSAGE)
                return@launch
            }

            _uiState.update { it.copy(isSaving = true) }
            val result = if (state.isEdit) {
                staffInteractor.updateStaff(
                    outletId = outletId,
                    staffId = editingStaffId.orEmpty(),
                    name = name,
                    email = email,
                    mobile = mobile,
                    username = username,
                    department = department,
                    type = type,
                )
            } else {
                val actingStaffId = userStorage.getLoggedInStaff().firstOrNull()?.id.orEmpty()
                if (actingStaffId.isBlank()) {
                    _uiState.update { it.copy(isSaving = false) }
                    _toastEvents.emit(GENERIC_ERROR_MESSAGE)
                    return@launch
                }
                staffInteractor.addStaff(
                    outletId = outletId,
                    actingStaffId = actingStaffId,
                    name = name,
                    email = email,
                    mobile = mobile,
                    username = username,
                    password = password,
                    department = department,
                    type = type,
                )
            }

            when (result) {
                is NetworkResult.Success -> {
                    staffListRefreshNotifier.notifyStaffListChanged()
                    _toastEvents.emit(result.data)
                    _successEvents.emit(Unit)
                }
                is NetworkResult.Error -> {
                    val message = when (val error = result.error) {
                        is NetworkError.Unknown -> error.userMessage
                        else -> error.userFacingMessage()
                    }
                    _toastEvents.emit(message)
                }
            }
            _uiState.update { it.copy(isSaving = false) }
        }
    }
}
