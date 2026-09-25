package com.droptechsolution.shared.ui.servicecatalog.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droptechsolution.shared.masterdata.interactor.MasterDataInteractor
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.services.interactor.ServiceCatalogInteractor
import com.droptechsolution.shared.services.models.ServiceCatalogItemUi
import com.droptechsolution.shared.ui.common.user.UserStorage
import com.droptechsolution.shared.ui.servicecatalog.ServiceCatalogRefreshNotifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ServiceCatalogListViewModel(
    private val catalogInteractor: ServiceCatalogInteractor,
    private val userStorage: UserStorage,
    private val masterDataInteractor: MasterDataInteractor,
    private val refreshNotifier: ServiceCatalogRefreshNotifier,
) : ViewModel() {

    private val _services = MutableStateFlow<List<ServiceCatalogItemUi>>(emptyList())
    val services = _services.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        viewModelScope.launch {
            refreshNotifier.events.collect { loadServices(forceRefresh = true) }
        }
    }

    fun loadServices(forceRefresh: Boolean = false) {
        if (!forceRefresh && _services.value.isNotEmpty()) return
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val outletId = userStorage.requireOutletId()
            val staffId = userStorage.getLoggedInStaff().firstOrNull()?.id.orEmpty()
            if (outletId.isBlank() || staffId.isBlank()) {
                _services.value = emptyList()
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

            when (val result = catalogInteractor.loadServices(outletId, staffId, labels)) {
                is NetworkResult.Success -> {
                    _services.value = result.data
                    if (result.data.isEmpty()) {
                        _errorMessage.value = "No services found"
                    }
                }
                is NetworkResult.Error -> {
                    _services.value = emptyList()
                    _errorMessage.value = result.error.userMessage
                }
            }
            _isLoading.value = false
        }
    }
}
