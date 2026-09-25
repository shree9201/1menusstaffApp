package com.droptechsolution.shared.ui.activity.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.services.interactor.ServicesInteractor
import com.droptechsolution.shared.services.models.ActivityListItemUi
import com.droptechsolution.shared.ui.common.user.UserStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ActivityListViewModel(
    private val servicesInteractor: ServicesInteractor,
    private val userStorage: UserStorage,
) : ViewModel() {

    private val _activities = MutableStateFlow<List<ActivityListItemUi>>(emptyList())
    val activities = _activities.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun loadActivities() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val staff = userStorage.getLoggedInStaff().firstOrNull()
            val outletId = staff?.userId?.ifBlank { null } ?: userStorage.requireOutletId()
            val staffId = staff?.id.orEmpty()

            if (outletId.isBlank() || staffId.isBlank()) {
                _activities.value = emptyList()
                _errorMessage.value = "User session not found. Please log in again."
                _isLoading.value = false
                return@launch
            }

            when (val result = servicesInteractor.loadActivityList(outletId, staffId)) {
                is NetworkResult.Success -> {
                    _activities.value = result.data
                    if (result.data.isEmpty()) {
                        _errorMessage.value = "No notifications found"
                    }
                }
                is NetworkResult.Error -> {
                    _activities.value = emptyList()
                    _errorMessage.value = result.error.userMessage
                }
            }
            _isLoading.value = false
        }
    }
}
