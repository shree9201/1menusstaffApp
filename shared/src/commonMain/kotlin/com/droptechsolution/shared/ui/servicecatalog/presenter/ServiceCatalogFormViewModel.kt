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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ServiceCatalogFormUiState(
    val id: String = "",
    val title: String = "",
    val information: String = "",
    val boxId: String = "",
    val actionBy: String = "",
    val aksDateTime: String = "NO",
    val reminderTime: String = "0",
    val escalationTime: String = "0",
    val priority: String = "Medium",
    val points: String = "0",
    val onHoldOption: String = "NO",
    val status: String = "YES",
    val actionByOptions: List<Pair<String, String>> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
)

class ServiceCatalogFormViewModel(
    private val catalogInteractor: ServiceCatalogInteractor,
    private val userStorage: UserStorage,
    private val masterDataInteractor: MasterDataInteractor,
    private val refreshNotifier: ServiceCatalogRefreshNotifier,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServiceCatalogFormUiState())
    val uiState = _uiState.asStateFlow()

    private val _toastEvents = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toastEvents = _toastEvents.asSharedFlow()

    private val _successEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val successEvents = _successEvents.asSharedFlow()

    private val yesNoOptions = listOf("YES" to "Yes", "NO" to "No")
    val priorityOptions = listOf("High" to "High", "Medium" to "Medium", "Low" to "Low")
    val toggleOptions = yesNoOptions

    fun load(serviceId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, id = serviceId) }
            if (masterDataInteractor.masterData.value == null) {
                masterDataInteractor.loadMasterData()
            }
            val actionByOptions = masterDataInteractor.masterData.value?.staffTypes
                ?.map { it.key to it.value }
                .orEmpty()

            val outletId = userStorage.requireOutletId()
            val staffId = userStorage.getLoggedInStaff().firstOrNull()?.id.orEmpty()
            if (outletId.isBlank() || staffId.isBlank()) {
                _uiState.update { it.copy(isLoading = false) }
                _toastEvents.emit(GENERIC_ERROR_MESSAGE)
                return@launch
            }

            val labels = actionByOptions.toMap()
            when (val result = catalogInteractor.loadServices(outletId, staffId, labels)) {
                is NetworkResult.Success -> {
                    val service = result.data.firstOrNull { it.id == serviceId }
                    if (service == null) {
                        _uiState.update { it.copy(isLoading = false, actionByOptions = actionByOptions) }
                        _toastEvents.emit("Service details not found")
                        return@launch
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            id = service.id,
                            title = service.title,
                            information = service.information,
                            boxId = service.boxId,
                            actionBy = service.actionBy.ifBlank { actionByOptions.firstOrNull()?.first.orEmpty() },
                            aksDateTime = service.aksDateTime.ifBlank { "NO" },
                            reminderTime = service.reminderTime,
                            escalationTime = service.escalationTime,
                            priority = service.priority.ifBlank { "Medium" },
                            points = service.points,
                            onHoldOption = service.onHoldOption.ifBlank { "NO" },
                            status = service.status.ifBlank { "YES" },
                            actionByOptions = actionByOptions,
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, actionByOptions = actionByOptions) }
                    _toastEvents.emit(result.error.userFacingMessage())
                }
            }
        }
    }

    fun onTitleChanged(value: String) = _uiState.update { it.copy(title = value) }
    fun onInformationChanged(value: String) = _uiState.update { it.copy(information = value) }
    fun onBoxIdChanged(value: String) = _uiState.update { it.copy(boxId = value) }
    fun onActionByChanged(value: String) = _uiState.update { it.copy(actionBy = value) }
    fun onAksDateTimeChanged(value: String) = _uiState.update { it.copy(aksDateTime = value) }
    fun onReminderTimeChanged(value: String) = _uiState.update { it.copy(reminderTime = value) }
    fun onEscalationTimeChanged(value: String) = _uiState.update { it.copy(escalationTime = value) }
    fun onPriorityChanged(value: String) = _uiState.update { it.copy(priority = value) }
    fun onPointsChanged(value: String) = _uiState.update { it.copy(points = value) }
    fun onHoldOptionChanged(value: String) = _uiState.update { it.copy(onHoldOption = value) }
    fun onStatusChanged(value: String) = _uiState.update { it.copy(status = value) }

    fun save() {
        val state = _uiState.value
        if (state.isSaving) return
        viewModelScope.launch {
            if (state.title.isBlank()) {
                _toastEvents.emit("Please enter service title")
                return@launch
            }
            if (state.actionBy.isBlank()) {
                _toastEvents.emit("Please select action by")
                return@launch
            }

            val outletId = userStorage.requireOutletId()
            val staffId = userStorage.getLoggedInStaff().firstOrNull()?.id.orEmpty()
            if (outletId.isBlank() || staffId.isBlank()) {
                _toastEvents.emit(GENERIC_ERROR_MESSAGE)
                return@launch
            }

            val service = ServiceCatalogItemUi(
                id = state.id,
                title = state.title,
                category = "",
                actionBy = state.actionBy,
                actionByLabel = state.actionBy,
                information = state.information,
                reminderTime = state.reminderTime,
                escalationTime = state.escalationTime,
                priority = state.priority,
                points = state.points,
                onHoldOption = state.onHoldOption,
                status = state.status,
                boxId = state.boxId,
                aksDateTime = state.aksDateTime,
                serviceId = "",
            )

            _uiState.update { it.copy(isSaving = true) }
            when (val result = catalogInteractor.updateService(outletId, staffId, service)) {
                is NetworkResult.Success -> {
                    refreshNotifier.notifyChanged()
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
