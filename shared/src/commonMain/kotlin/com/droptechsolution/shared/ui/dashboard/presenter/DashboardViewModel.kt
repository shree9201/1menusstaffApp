package com.droptechsolution.shared.ui.dashboard.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droptechsolution.shared.masterdata.interactor.MasterDataInteractor
import com.droptechsolution.shared.ui.common.user.UserSession
import com.droptechsolution.shared.ui.common.user.UserStorage
import com.droptechsolution.shared.ui.common.user.toUserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val userStorage: UserStorage,
    private val masterDataInteractor: MasterDataInteractor,
) : ViewModel() {

    private val _userSession = MutableStateFlow<UserSession?>(null)
    val userSession = _userSession.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                userStorage.getLoggedInStaff(),
                masterDataInteractor.masterData,
            ) { staff, masterData ->
                staff?.toUserSession(masterData)
            }.collect { session ->
                _userSession.value = session
            }
        }
        loadSession()
    }

    fun loadSession() {
        viewModelScope.launch {
            if (masterDataInteractor.masterData.value == null) {
                masterDataInteractor.loadMasterData()
            }
            val staff = userStorage.getLoggedInStaff().firstOrNull()
            _userSession.value = staff?.toUserSession(masterDataInteractor.masterData.value)
        }
    }
}
