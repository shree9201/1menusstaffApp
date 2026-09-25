package com.droptechsolution.shared.ui.servicecatalog.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droptechsolution.shared.masterdata.interactor.MasterDataInteractor
import com.droptechsolution.shared.network.NetworkError
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.services.interactor.ServiceCatalogInteractor
import com.droptechsolution.shared.services.models.ServiceCatalogItemUi
import com.droptechsolution.shared.ui.common.GENERIC_ERROR_MESSAGE
import com.droptechsolution.shared.ui.common.user.UserStorage
import com.droptechsolution.shared.ui.servicecatalog.ServiceCatalogRefreshNotifier
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ServiceCatalogDetailsViewModel(
    private val catalogInteractor: ServiceCatalogInteractor,
    private val userStorage: UserStorage,
    private val masterDataInteractor: MasterDataInteractor,
    private val refreshNotifier: ServiceCatalogRefreshNotifier,
) : ViewModel() {

    private val _details = MutableStateFlow<ServiceCatalogItemUi?>(null)
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

    private var currentServiceId: String = ""

    init {
        viewModelScope.launch {
            refreshNotifier.events.collect {
                if (currentServiceId.isNotBlank()) loadDetails(currentServiceId)
            }
        }
    }

    fun loadDetails(serviceId: String) {
        currentServiceId = serviceId
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val session = sessionIds()
            if (session == null) {
                _details.value = null
                _errorMessage.value = "Outlet ID not found. Please log in again."
                _isLoading.value = false
                return@launch
            }
            if (masterDataInteractor.masterData.value == null) {
                masterDataInteractor.loadMasterData()
            }
            val labels = masterDataInteractor.masterData.value?.staffTypes
                ?.associate { it.key to it.value }
                .orEmpty()

            when (val result = catalogInteractor.loadServices(session.first, session.second, labels)) {
                is NetworkResult.Success -> {
                    val service = result.data.firstOrNull { it.id == serviceId }
                    if (service == null) {
                        _details.value = null
                        _errorMessage.value = "Service details not found"
                    } else {
                        _details.value = service
                    }
                }
                is NetworkResult.Error -> {
                    _details.value = null
                    _errorMessage.value = result.error.userMessage
                }
            }
            _isLoading.value = false
        }
    }

    fun deleteService() {
        if (currentServiceId.isBlank() || _isDeleting.value) return
        viewModelScope.launch {
            val session = sessionIds()
            if (session == null) {
                _toastEvents.emit(GENERIC_ERROR_MESSAGE)
                return@launch
            }
            _isDeleting.value = true
            when (val result = catalogInteractor.deleteService(session.first, session.second, currentServiceId)) {
                is NetworkResult.Success -> {
                    refreshNotifier.notifyChanged()
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

    private suspend fun sessionIds(): Pair<String, String>? {
        val outletId = userStorage.requireOutletId()
        val staffId = userStorage.getLoggedInStaff().firstOrNull()?.id.orEmpty()
        if (outletId.isBlank() || staffId.isBlank()) return null
        return outletId to staffId
    }
}
