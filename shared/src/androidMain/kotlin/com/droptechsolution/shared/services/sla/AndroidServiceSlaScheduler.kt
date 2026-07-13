package com.droptechsolution.shared.services.sla

class AndroidServiceSlaScheduler : ServiceSlaScheduler {
    override fun startTracking(requestId: String, reminderMinutes: Int, startedAtEpochMs: Long) {
        ServiceSlaWorkScheduler.enqueue(applicationContext, requestId)
    }

    override fun stopTracking(requestId: String) {
        ServiceSlaWorkScheduler.cancel(applicationContext, requestId)
    }
}
