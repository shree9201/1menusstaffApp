package com.droptechsolution.shared.services.sla

interface ServiceSlaScheduler {
    fun startTracking(requestId: String, reminderMinutes: Int, startedAtEpochMs: Long)
    fun stopTracking(requestId: String)
}
