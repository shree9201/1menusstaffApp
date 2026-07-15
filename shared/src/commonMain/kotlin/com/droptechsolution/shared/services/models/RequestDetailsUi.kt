package com.droptechsolution.shared.services.models

data class RequestDetailsUi(
    val id: String,
    val requestCode: String,
    val title: String,
    val roomNumber: String,
    val status: String,
    val statusDisplay: RequestStatusDisplay,
    val priorityLabel: String,
    val guestLabel: String,
    val requestedMeta: String,
    val department: String,
    val reminderTime: String,
    val reminderTimeMinutes: Int,
    val estimatedDuration: String,
    val priorityShort: String,
    val slaCardTitle: String,
    val workStartedAt: String,
    val note: String,
    val points: Int,
    val timelineSteps: List<TimelineStepUi>,
    val availableActions: List<RequestAction>,
)

data class TimelineStepUi(
    val headline: String,
    val description: String,
    val timeLabel: String,
    val durationFromPrevious: String?,
    val isCompleted: Boolean,
    val isCurrent: Boolean,
)

enum class RequestStatusDisplay {
    NEW,
    ACCEPTED,
    IN_PROGRESS,
    ON_HOLD,
    COMPLETED,
    REJECTED,
    ESCALATED,
    UNKNOWN,
}

fun RequestDetails.toUi(outletService: OutletService? = null): RequestDetailsUi {
    val service = serviceDetails ?: request.serviceDetails.firstOrNull() ?: outletService
    val displayTitle = request.displayTitle()
    val priority = service?.priority?.toTaskPriority() ?: TaskPriority.MEDIUM
    val department = service?.actionBy.toDepartmentLabel()
    val reminderDuration = service.toReminderDurationLabel()
    val reminderMinutes = service.toReminderMinutes()
    val estDuration = service.toEscalationDurationLabel()
    val priorityShort = priority.toShortLabel()
    val slaCardTitle = "Room ${request.roomId} — ${displayTitle.ifBlank { "Service" }}"
    val statusDisplay = request.status.toStatusDisplay()
    val timelineSteps = activities.toTimelineSteps(request.status)

    return RequestDetailsUi(
        id = request.id,
        requestCode = request.requestCode,
        title = displayTitle,
        roomNumber = request.roomId,
        status = request.status,
        statusDisplay = statusDisplay,
        priorityLabel = priority.toLabel(),
        guestLabel = request.guestName.takeIf { it.isNotBlank() }?.let { "Guest: $it" } ?: "Guest: —",
        requestedMeta = buildRequestedMeta(request.createdDate, estDuration),
        department = department,
        reminderTime = reminderDuration,
        reminderTimeMinutes = reminderMinutes,
        estimatedDuration = estDuration,
        priorityShort = priorityShort,
        slaCardTitle = slaCardTitle,
        workStartedAt = request.startTime,
        note = request.note,
        points = request.points,
        timelineSteps = timelineSteps,
        availableActions = request.status.toAvailableActions(),
    )
}

fun OutletService.toDetailsUi(): RequestDetailsUi =
    RequestDetailsUi(
        id = id,
        requestCode = "SRV-$id",
        title = title,
        roomNumber = boxId,
        status = status,
        statusDisplay = if (status.equals("YES", ignoreCase = true)) RequestStatusDisplay.IN_PROGRESS else RequestStatusDisplay.UNKNOWN,
        priorityLabel = priority.toTaskPriority().toLabel(),
        guestLabel = "Guest: —",
        requestedMeta = "Outlet service",
        department = actionBy.toDepartmentLabel(),
        reminderTime = toReminderDurationLabel(),
        reminderTimeMinutes = toReminderMinutes(),
        estimatedDuration = toEscalationDurationLabel(),
        priorityShort = priority.toTaskPriority().toShortLabel(),
        slaCardTitle = "Box $boxId — ${title.ifBlank { "Service" }}",
        workStartedAt = "",
        note = information,
        points = points,
        timelineSteps = emptyList(),
        availableActions = emptyList(),
    )

private fun buildRequestedMeta(createdDate: String, sla: String): String {
    val ago = createdDate.toRelativeTimeLabel()
    return "Requested $ago · SLA $sla"
}

private fun String.toRelativeTimeLabel(): String {
    if (isBlank()) return "recently"
    val timePart = substringAfter(" ", "").takeIf { it.length >= 5 }?.take(5)
    return timePart?.let { "at $it" } ?: "recently"
}

private fun String?.toDepartmentLabel(): String = when (this?.uppercase()) {
    "HK" -> "Housekeeping"
    "FO" -> "Front Office"
    "MTNS", "MAINTENANCE" -> "Maintenance"
    "KITCHEN" -> "Kitchen"
    "SPA" -> "Spa"
    null, "" -> "General"
    else -> this
}

private fun OutletService?.toReminderDurationLabel(): String {
    val minutes = this?.reminderTime?.toIntOrNull()
    return when {
        minutes != null && minutes > 0 -> "$minutes min"
        else -> "—"
    }
}

private fun OutletService?.toReminderMinutes(): Int =
    this?.reminderTime?.toIntOrNull()?.coerceAtLeast(0) ?: 0

private fun OutletService?.toEscalationDurationLabel(): String {
    val minutes = this?.escalationTime?.toIntOrNull()
    return when {
        minutes != null && minutes > 0 -> "$minutes min"
        else -> "25 min"
    }
}

private fun TaskPriority.toLabel(): String = when (this) {
    TaskPriority.HIGH -> "High priority"
    TaskPriority.MEDIUM -> "Medium priority"
    TaskPriority.LOW -> "Low priority"
}

private fun TaskPriority.toShortLabel(): String = when (this) {
    TaskPriority.HIGH -> "High"
    TaskPriority.MEDIUM -> "Med"
    TaskPriority.LOW -> "Low"
}

fun String.toStatusDisplay(): RequestStatusDisplay = when (uppercase()) {
    "NEW" -> RequestStatusDisplay.NEW
    "ACCEPT", "ACCEPTED" -> RequestStatusDisplay.ACCEPTED
    "START", "STARTED" -> RequestStatusDisplay.IN_PROGRESS
    "HOLD", "PAUSE", "PAUSED" -> RequestStatusDisplay.ON_HOLD
    "CLOSE", "CLOSED", "DONE", "END" -> RequestStatusDisplay.COMPLETED
    "REJECT", "REJECTED" -> RequestStatusDisplay.REJECTED
    "ESCALATED" -> RequestStatusDisplay.ESCALATED
    else -> RequestStatusDisplay.UNKNOWN
}

fun String.toAvailableActions(): List<RequestAction> = when (toStatusDisplay()) {
    RequestStatusDisplay.NEW -> listOf(RequestAction.ACCEPT, RequestAction.REJECT)
    RequestStatusDisplay.ACCEPTED -> listOf(RequestAction.HOLD_ESCALATE, RequestAction.START)
    RequestStatusDisplay.IN_PROGRESS -> listOf(RequestAction.PAUSE, RequestAction.PASS, RequestAction.COMPLETE)
    RequestStatusDisplay.ON_HOLD -> listOf(RequestAction.START, RequestAction.COMPLETE)
    else -> emptyList()
}

fun List<RequestActivity>.toTimelineSteps(currentStatus: String): List<TimelineStepUi> {
    if (isEmpty()) return emptyList()

    val orderedKeys = listOf("OPEN", "ACCEPT", "START", "END", "DONE")
    val deduped = linkedMapOf<String, RequestActivity>()
    forEach { activity ->
        val key = activity.status.toTimelineKey()
        deduped[key] = activity
    }

    val steps = orderedKeys.mapNotNull { key -> deduped[key]?.let { key to it } }
    if (steps.isEmpty()) {
        return mapIndexed { index, activity ->
            activity.toTimelineStep(
                isCompleted = index < lastIndex,
                isCurrent = index == lastIndex,
                durationFromPrevious = if (index == 0) null else computeDurationLabel(get(index - 1).dateTime, activity.dateTime),
            )
        }
    }

    val currentKey = currentStatus.toTimelineKey()
    val currentIndex = steps.indexOfLast { it.first == currentKey }.coerceAtLeast(0)

    return steps.mapIndexed { index, (_, activity) ->
        activity.toTimelineStep(
            isCompleted = index < currentIndex || currentKey == "DONE",
            isCurrent = index == currentIndex && currentKey != "DONE",
            durationFromPrevious = if (index == 0) null else computeDurationLabel(steps[index - 1].second.dateTime, activity.dateTime),
        )
    }
}

private fun RequestActivity.toTimelineStep(
    isCompleted: Boolean,
    isCurrent: Boolean,
    durationFromPrevious: String?,
): TimelineStepUi {
    val (headline, description) = status.toTimelineCopy()
    return TimelineStepUi(
        headline = headline,
        description = description,
        timeLabel = dateTime.toDisplayTime(),
        durationFromPrevious = durationFromPrevious,
        isCompleted = isCompleted,
        isCurrent = isCurrent,
    )
}

private fun String.toTimelineKey(): String = when (uppercase()) {
    "NEW", "OPEN", "ASSIGN" -> "OPEN"
    "ACCEPT", "ACCEPTED" -> "ACCEPT"
    "START", "STARTED" -> "START"
    "END" -> "END"
    "CLOSE", "CLOSED", "DONE" -> "DONE"
    "HOLD", "PAUSE", "PAUSED" -> "START"
    else -> uppercase()
}

private fun String.toTimelineCopy(): Pair<String, String> = when (uppercase()) {
    "NEW", "OPEN", "ASSIGN" -> "Open" to "Request received"
    "ACCEPT", "ACCEPTED" -> "Accepted" to "Staff accepted request"
    "START", "STARTED" -> "Started" to "Service timer began"
    "END" -> "Ended" to "Service completed"
    "CLOSE", "CLOSED", "DONE" -> "Done" to "Request closed"
    "HOLD", "PAUSE", "PAUSED" -> "Paused" to "Request on hold"
    "ESCALATED" -> "Escalated" to "Request escalated"
    "REJECT", "REJECTED" -> "Rejected" to "Request rejected"
    else -> this to "Status updated"
}

private fun computeDurationLabel(previous: String, current: String): String? {
    val prevMinutes = previous.toMinutesOfDay() ?: return null
    val currentMinutes = current.toMinutesOfDay() ?: return null
    val diff = (currentMinutes - prevMinutes).coerceAtLeast(0)
    if (diff == 0) return null
    return "+${diff}m from previous"
}

private fun String.toMinutesOfDay(): Int? {
    val segment = substringAfter(" ", "").ifBlank { this }
    val parts = segment.split(":")
    if (parts.size < 2) return null
    val hours = parts[0].toIntOrNull() ?: return null
    val minutes = parts[1].toIntOrNull() ?: return null
    return hours * 60 + minutes
}
