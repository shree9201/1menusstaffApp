package com.droptechsolution.shared.services.sla

data class ServiceSlaSession(
    val requestId: String,
    val reminderMinutes: Int,
    val startedAtEpochMs: Long,
    val isRunning: Boolean,
)

data class ServiceSlaProgressUi(
    val title: String,
    val slaLabel: String,
    val elapsedLabel: String,
    val remainingLabel: String,
    val progressPercent: Int,
    val priorityLabel: String,
    val progressFraction: Float,
    val isActive: Boolean,
    val isComplete: Boolean,
)
