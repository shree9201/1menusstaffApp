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
        createdAt: String?,
        workStartedAt: String?,
        statusDisplay: RequestStatusDisplay,
    ) {
        if (reminderMinutes <= 0) return
        if (statusDisplay.isTerminal()) {
            stop(requestId)
            return
        }

        val startedAt = ServiceSlaCalculator.parseStartTimeEpochMs(createdAt.orEmpty())
            ?: ServiceSlaCalculator.parseStartTimeEpochMs(workStartedAt.orEmpty())
            ?: ServiceSlaCalculator.nowEpochMs()
        val existing = repository.getSession()
        if (
            existing?.requestId == requestId &&
            existing.isRunning &&
            existing.startedAtEpochMs == startedAt
        ) {
            return
        }
        start(requestId, reminderMinutes, startedAt)
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
        createdAt: String?,
        updatedAt: String?,
        workStartedAt: String?,
        workEndedAt: String?,
        statusDisplay: RequestStatusDisplay,
    ): ServiceSlaProgressUi? {
        if (reminderMinutes <= 0) return null

        val session = repository.getSession()
        val activeSession = session?.takeIf { it.requestId == requestId }
        val startedAt = ServiceSlaCalculator.parseStartTimeEpochMs(createdAt.orEmpty())
            ?: ServiceSlaCalculator.parseStartTimeEpochMs(workStartedAt.orEmpty())
            ?: activeSession?.startedAtEpochMs
            ?: ServiceSlaCalculator.nowEpochMs()
        val isCompleted = statusDisplay.isCompleted()
        val endedAt = if (isCompleted) {
            ServiceSlaCalculator.parseStartTimeEpochMs(updatedAt.orEmpty())
                ?: ServiceSlaCalculator.parseStartTimeEpochMs(workEndedAt.orEmpty())
                ?: ServiceSlaCalculator.nowEpochMs()
        } else {
            ServiceSlaCalculator.nowEpochMs()
        }
        val isRunning = !statusDisplay.isTerminal()

        return ServiceSlaCalculator.calculate(
            title = title,
            reminderMinutes = reminderMinutes,
            priorityLabel = priorityLabel,
            startedAtEpochMs = startedAt,
            nowEpochMs = endedAt.coerceAtLeast(startedAt),
            isRunning = isRunning,
        )
    }
}

private fun RequestStatusDisplay.isCompleted(): Boolean =
    this == RequestStatusDisplay.COMPLETED

private fun RequestStatusDisplay.isTerminal(): Boolean =
    isCompleted() || this == RequestStatusDisplay.REJECTED
