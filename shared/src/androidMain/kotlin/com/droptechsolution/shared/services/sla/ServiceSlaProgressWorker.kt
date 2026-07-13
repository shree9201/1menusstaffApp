package com.droptechsolution.shared.services.sla

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.droptechsolution.shared.common.data.DataStoreProvider

class ServiceSlaProgressWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val requestId = ServiceSlaWorkScheduler.readRequestId(inputData) ?: return Result.failure()
        val repository = ServiceSlaRepository(DataStoreProvider.create())
        val session = repository.getSession() ?: return Result.success()

        if (session.requestId != requestId || !session.isRunning) {
            return Result.success()
        }

        val progress = ServiceSlaCalculator.calculate(
            title = "",
            reminderMinutes = session.reminderMinutes,
            priorityLabel = "",
            startedAtEpochMs = session.startedAtEpochMs,
            isRunning = true,
        )

        if (progress.isComplete) {
            repository.stopSession(requestId)
            ServiceSlaWorkScheduler.cancel(applicationContext, requestId)
            return Result.success()
        }

        ServiceSlaWorkScheduler.scheduleNextTick(applicationContext, requestId)
        return Result.success()
    }
}
