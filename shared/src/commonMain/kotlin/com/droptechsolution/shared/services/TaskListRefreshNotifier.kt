package com.droptechsolution.shared.services

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class TaskListRefreshNotifier {
    private val _events = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    fun notifyTaskListChanged() {
        _events.tryEmit(Unit)
    }
}
