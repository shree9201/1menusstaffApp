package com.droptechsolution.shared.ui.staff

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class StaffListRefreshNotifier {
    private val _events = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    fun notifyStaffListChanged() {
        _events.tryEmit(Unit)
    }
}
