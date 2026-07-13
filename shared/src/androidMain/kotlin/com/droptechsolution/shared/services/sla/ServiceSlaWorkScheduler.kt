package com.droptechsolution.shared.services.sla

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

internal lateinit var applicationContext: Context

fun initAndroidSlaScheduler(appContext: Context) {
    applicationContext = appContext.applicationContext
}

object ServiceSlaWorkScheduler {
    private const val WORK_PREFIX = "service_sla_progress_"
    private const val KEY_REQUEST_ID = "request_id"

    fun enqueue(context: Context, requestId: String) {
        val workName = workName(requestId)
        val request = OneTimeWorkRequestBuilder<ServiceSlaProgressWorker>()
            .setInitialDelay(1, TimeUnit.SECONDS)
            .setInputData(workDataOf(KEY_REQUEST_ID to requestId))
            .addTag(workName)
            .build()

        WorkManager.getInstance(context.applicationContext)
            .enqueueUniqueWork(workName, ExistingWorkPolicy.REPLACE, request)
    }

    fun scheduleNextTick(context: Context, requestId: String) {
        enqueue(context, requestId)
    }

    fun cancel(context: Context, requestId: String) {
        WorkManager.getInstance(context.applicationContext)
            .cancelUniqueWork(workName(requestId))
    }

    internal fun workName(requestId: String): String = "$WORK_PREFIX$requestId"

    internal fun readRequestId(inputData: androidx.work.Data): String? =
        inputData.getString(KEY_REQUEST_ID)
}
