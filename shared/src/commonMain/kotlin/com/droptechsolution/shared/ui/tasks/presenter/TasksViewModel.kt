package com.droptechsolution.shared.ui.tasks.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.services.interactor.ServicesInteractor
import com.droptechsolution.shared.services.models.ServiceRequestRowUi
import com.droptechsolution.shared.ui.common.user.UserStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TasksViewModel(
    private val servicesInteractor: ServicesInteractor,
    private val userStorage: UserStorage,
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<ServiceRequestRowUi>>(emptyList())
    val tasks = _tasks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun loadTasks() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val outletId = userStorage.requireOutletId()
            if (outletId.isBlank()) {
                _tasks.value = emptyList()
                _errorMessage.value = "Outlet ID not found. Please log in again."
                _isLoading.value = false
                return@launch
            }

            when (val result = servicesInteractor.loadAllTasks(outletId, activeOnly = false)) {
                is NetworkResult.Success -> {
                    _tasks.value = result.data.rows
                }
                is NetworkResult.Error -> {
                    _tasks.value = emptyList()
                    _errorMessage.value = result.error.userMessage
                }
            }
            _isLoading.value = false
        }
    }
}
