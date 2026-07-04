package com.droptechsolution.shared.ui.tasks.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.services.interactor.ServicesInteractor
import com.droptechsolution.shared.services.models.RequestDetailsUi
import com.droptechsolution.shared.services.models.RequestSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RequestDetailsViewModel(
    private val servicesInteractor: ServicesInteractor,
) : ViewModel() {

    private val _details = MutableStateFlow<RequestDetailsUi?>(null)
    val details = _details.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    var outletId: String = "5"

    fun loadDetails(requestId: String, source: RequestSource) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
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
}
