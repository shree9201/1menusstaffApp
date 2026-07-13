package com.droptechsolution.shared.services.sla

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

object ServiceSlaCalculator {

    fun nowEpochMs(): Long = Clock.System.now().toEpochMilliseconds()

    fun calculate(
        title: String,
        reminderMinutes: Int,
        priorityLabel: String,
        startedAtEpochMs: Long,
        nowEpochMs: Long = nowEpochMs(),
        isRunning: Boolean,
    ): ServiceSlaProgressUi {
        val totalMs = reminderMinutes * 60_000L
        val elapsedMs = if (isRunning) {
            (nowEpochMs - startedAtEpochMs).coerceAtLeast(0)
        } else {
            0L
        }
        val progressFraction = if (totalMs > 0) {
            (elapsedMs.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
        val remainingMs = (totalMs - elapsedMs).coerceAtLeast(0)

        return ServiceSlaProgressUi(
            title = title,
            slaLabel = "SLA: $reminderMinutes min SLA",
            elapsedLabel = formatClock(elapsedMs),
            remainingLabel = formatClock(remainingMs),
            progressPercent = (progressFraction * 100).toInt(),
            priorityLabel = priorityLabel,
            progressFraction = progressFraction,
            isActive = isRunning,
            isComplete = progressFraction >= 1f,
        )
    }

    fun formatClock(durationMs: Long): String {
        val totalSeconds = durationMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    }

    fun parseStartTimeEpochMs(startTime: String): Long? {
        val parts = startTime.trim().split(" ")
        if (parts.size < 2) return null
        val dateParts = parts[0].split("-").mapNotNull { it.toIntOrNull() }
        val timeParts = parts[1].split(":").mapNotNull { it.toIntOrNull() }
        if (dateParts.size < 3 || timeParts.size < 2) return null

        return runCatching {
            LocalDateTime(
                year = dateParts[0],
                monthNumber = dateParts[1],
                dayOfMonth = dateParts[2],
                hour = timeParts[0],
                minute = timeParts[1],
                second = timeParts.getOrElse(2) { 0 },
            ).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        }.getOrNull()
    }
}
