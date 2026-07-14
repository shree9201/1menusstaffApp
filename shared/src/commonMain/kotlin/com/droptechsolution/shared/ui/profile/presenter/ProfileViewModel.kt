package com.droptechsolution.shared.ui.profile.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droptechsolution.shared.masterdata.interactor.MasterDataInteractor
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffDetails
import com.droptechsolution.shared.ui.common.user.StaffType
import com.droptechsolution.shared.ui.common.user.UserStorage
import com.droptechsolution.shared.ui.common.user.toStaffType
import com.droptechsolution.shared.ui.common.user.toUserSession
import com.droptechsolution.shared.ui.profile.models.ProfileUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userStorage: UserStorage,
    private val masterDataInteractor: MasterDataInteractor,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val _logoutEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val logoutEvents = _logoutEvents.asSharedFlow()

    init {
        viewModelScope.launch {
            combine(
                userStorage.getLoggedInStaff(),
                masterDataInteractor.masterData,
            ) { staff, masterData ->
                staff?.let {
                    val session = it.toUserSession(masterData)
                    it.toProfileUiState(
                        roleLabel = session.staffTypeLabel,
                        departmentLabel = session.departmentLabel,
                    )
                }
            }.collect { state ->
                if (state != null) {
                    _uiState.value = state
                }
            }
        }
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            if (masterDataInteractor.masterData.value == null) {
                masterDataInteractor.loadMasterData()
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userStorage.clearSession()
            _logoutEvents.emit(Unit)
        }
    }

    private fun StaffDetails.toProfileUiState(
        roleLabel: String,
        departmentLabel: String,
    ): ProfileUiState {
        val staffType = type.toStaffType()
        val isManager = staffType in MANAGER_PROFILE_TYPES
        return ProfileUiState(
            name = name,
            roleLabel = roleLabel.ifBlank { staffType.defaultLabel() },
            initials = name.toInitials(),
            departmentLabel = departmentLabel.ifBlank {
                department?.takeIf { it.isNotBlank() } ?: staffType.defaultLabel()
            },
            username = username,
            isManagerProfile = isManager,
        )
    }

    companion object {
        private val MANAGER_PROFILE_TYPES = setOf(
            StaffType.FOMGR,
            StaffType.FOSU,
            StaffType.HKMGR,
            StaffType.HKSU,
        )
    }
}

private fun String.toInitials(): String {
    val parts = trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts.first().take(2).uppercase()
        else -> "${parts.first().first()}${parts.last().first()}".uppercase()
    }
}
