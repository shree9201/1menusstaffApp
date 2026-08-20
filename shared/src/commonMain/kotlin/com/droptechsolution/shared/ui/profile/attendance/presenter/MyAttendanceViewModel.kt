package com.droptechsolution.shared.ui.profile.attendance.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffAttendanceDetail
import com.droptechsolution.shared.ui.common.GENERIC_ERROR_MESSAGE
import com.droptechsolution.shared.ui.common.user.UserStorage
import com.droptechsolution.shared.ui.profile.attendance.MyAttendanceMapper
import com.droptechsolution.shared.ui.profile.attendance.models.MyAttendanceUiState
import com.droptechsolution.shared.ui.staff.interactor.StaffInteractor
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyAttendanceViewModel(
    private val staffInteractor: StaffInteractor,
    private val userStorage: UserStorage,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MyAttendanceMapper.buildUiState(
            selectedMonthKey = MyAttendanceMapper.recentMonthOptions().firstOrNull()?.key.orEmpty(),
            records = emptyList(),
            isLoading = false,
        ),
    )
    val uiState = _uiState.asStateFlow()

    private val _toastEvents = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toastEvents = _toastEvents.asSharedFlow()

    private var allRecords: List<StaffAttendanceDetail> = emptyList()
    private var selectedMonthKey: String = _uiState.value.availableMonths.firstOrNull()?.key.orEmpty()
    private var hasLoaded = false

    fun loadAttendance(forceRefresh: Boolean = false) {
        if (hasLoaded && !forceRefresh && allRecords.isNotEmpty()) return

        viewModelScope.launch {
            updateState(isLoading = true)

            val staff = userStorage.getLoggedInStaff().firstOrNull()
            val outletId = staff?.userId.orEmpty()
            val staffId = staff?.id.orEmpty()
            if (outletId.isBlank() || staffId.isBlank()) {
                updateState(isLoading = false)
                _toastEvents.emit(GENERIC_ERROR_MESSAGE)
                return@launch
            }

            when (val result = staffInteractor.loadStaffAttendances(outletId, staffId)) {
                is NetworkResult.Success -> {
                    allRecords = result.data
                    hasLoaded = true
                    updateState(isLoading = false)
                }
                is NetworkResult.Error -> {
                    updateState(isLoading = false)
                    _toastEvents.emit(GENERIC_ERROR_MESSAGE)
                }
            }
        }
    }

    fun onMonthSelected(monthKey: String) {
        selectedMonthKey = monthKey
        updateState(isLoading = false)
    }

    private fun updateState(isLoading: Boolean) {
        _uiState.update {
            MyAttendanceMapper.buildUiState(
                selectedMonthKey = selectedMonthKey,
                records = allRecords,
                isLoading = isLoading,
            )
        }
    }
}
