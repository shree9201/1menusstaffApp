package com.droptechsolution.shared.services.sla

import com.droptechsolution.shared.services.models.RequestStatusDisplay

class ServiceSlaTracker(
    private val repository: ServiceSlaRepository,
    private val scheduler: ServiceSlaScheduler,
) {
    suspend fun start(
        requestId: String,
        reminderMinutes: Int,
        startedAtEpochMs: Long = ServiceSlaCalculator.nowEpochMs(),
    ) {
        if (reminderMinutes <= 0) return
        val session = ServiceSlaSession(
            requestId = requestId,
            reminderMinutes = reminderMinutes,
            startedAtEpochMs = startedAtEpochMs,
            isRunning = true,
        )
        repository.saveSession(session)
        scheduler.startTracking(requestId, reminderMinutes, startedAtEpochMs)
    }

    suspend fun stop(requestId: String) {
        repository.stopSession(requestId)
        scheduler.stopTracking(requestId)
    }

    suspend fun clear(requestId: String) {
        repository.clearSession(requestId)
        scheduler.stopTracking(requestId)
    }

    suspend fun ensureSessionForInProgressRequest(
        requestId: String,
        reminderMinutes: Int,
        workStartedAt: String?,
        statusDisplay: RequestStatusDisplay,
    ) {
        if (reminderMinutes <= 0) return
        val existing = repository.getSession()
        if (existing?.requestId == requestId && existing.isRunning) return

        if (statusDisplay == RequestStatusDisplay.IN_PROGRESS) {
            val startedAt = ServiceSlaCalculator.parseStartTimeEpochMs(workStartedAt.orEmpty())
                ?: ServiceSlaCalculator.nowEpochMs()
            start(requestId, reminderMinutes, startedAt)
        }
    }

    suspend fun getProgress(
        requestId: String,
        reminderMinutes: Int,
        title: String,
        priorityLabel: String,
        statusDisplay: RequestStatusDisplay,
    ): ServiceSlaProgressUi? {
        if (reminderMinutes <= 0) return null

        val session = repository.getSession()
        val isRunning = session?.requestId == requestId && session.isRunning
        val startedAt = when {
            isRunning -> session.startedAtEpochMs
            statusDisplay == RequestStatusDisplay.IN_PROGRESS ->
                ServiceSlaCalculator.parseStartTimeEpochMs("") ?: ServiceSlaCalculator.nowEpochMs()
            else -> ServiceSlaCalculator.nowEpochMs()
        }

        return ServiceSlaCalculator.calculate(
            title = title,
            reminderMinutes = reminderMinutes,
            priorityLabel = priorityLabel,
            startedAtEpochMs = startedAt,
            isRunning = isRunning || statusDisplay == RequestStatusDisplay.IN_PROGRESS,
        )
    }

    suspend fun getProgressForSession(
        requestId: String,
        reminderMinutes: Int,
        title: String,
        priorityLabel: String,
        workStartedAt: String?,
        statusDisplay: RequestStatusDisplay,
    ): ServiceSlaProgressUi? {
        if (reminderMinutes <= 0) return null

        val session = repository.getSession()
        val activeSession = session?.takeIf { it.requestId == requestId }
        val isRunning = activeSession?.isRunning == true
        val startedAt = when {
            isRunning -> activeSession.startedAtEpochMs
            statusDisplay == RequestStatusDisplay.IN_PROGRESS -> {
                ServiceSlaCalculator.parseStartTimeEpochMs(workStartedAt.orEmpty())
                    ?: ServiceSlaCalculator.nowEpochMs()
            }
            else -> ServiceSlaCalculator.nowEpochMs()
        }

        return ServiceSlaCalculator.calculate(
            title = title,
            reminderMinutes = reminderMinutes,
            priorityLabel = priorityLabel,
            startedAtEpochMs = startedAt,
            isRunning = isRunning,
        )
    }
}
