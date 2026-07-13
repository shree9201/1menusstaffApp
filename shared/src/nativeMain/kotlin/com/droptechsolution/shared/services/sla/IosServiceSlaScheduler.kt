package com.droptechsolution.shared.services.sla

class IosServiceSlaScheduler : ServiceSlaScheduler {
    override fun startTracking(requestId: String, reminderMinutes: Int, startedAtEpochMs: Long) = Unit

    override fun stopTracking(requestId: String) = Unit
}
