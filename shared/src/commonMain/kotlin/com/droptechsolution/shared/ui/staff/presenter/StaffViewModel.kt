package com.droptechsolution.shared.ui.staff.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.ui.common.user.UserStorage
import com.droptechsolution.shared.ui.staff.interactor.StaffInteractor
import com.droptechsolution.shared.ui.staff.models.StaffMemberUi
import com.droptechsolution.shared.ui.staff.models.StaffSummaryUi
import com.droptechsolution.shared.ui.staff.models.toStaffMemberUi
import com.droptechsolution.shared.ui.staff.models.toSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StaffViewModel(
    private val staffInteractor: StaffInteractor,
    private val userStorage: UserStorage,
) : ViewModel() {

    private val _staffMembers = MutableStateFlow<List<StaffMemberUi>>(emptyList())
    val staffMembers = _staffMembers.asStateFlow()

    private val _summary = MutableStateFlow(StaffSummaryUi(0, 0))
    val summary = _summary.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private var hasLoaded = false

    fun loadStaff(forceRefresh: Boolean = false) {
        if (hasLoaded && !forceRefresh && _staffMembers.value.isNotEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val outletId = userStorage.requireOutletId()
            if (outletId.isBlank()) {
                _staffMembers.value = emptyList()
                _summary.value = StaffSummaryUi(0, 0)
                _errorMessage.value = "Outlet ID not found. Please log in again."
                _isLoading.value = false
                return@launch
            }

            when (val result = staffInteractor.loadStaffList(outletId)) {
                is NetworkResult.Success -> {
                    val members = result.data.map { it.toStaffMemberUi() }
                    _staffMembers.value = members
                    _summary.value = members.toSummary()
                    hasLoaded = true
                }
                is NetworkResult.Error -> {
                    _errorMessage.value = result.error.userMessage
                }
            }
            _isLoading.value = false
        }
    }
}
