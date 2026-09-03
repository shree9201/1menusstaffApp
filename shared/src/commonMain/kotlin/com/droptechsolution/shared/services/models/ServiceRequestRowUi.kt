package com.droptechsolution.shared.services.models

enum class RequestSource {
    ROOM,
    OUTLET,
}

enum class TaskPriority {
    HIGH,
    MEDIUM,
    LOW,
}

enum class TaskStatus {
    NEW,
    ACCEPT,
    ASSIGN,
    START,
    HOLD,
    END,
    DONE,
    CLOSE,
    REJECT,
    REOPEN,
    DEFAULT
}

enum class TaskActionType {
    ACCEPT,
    START,
    NONE,
}

data class ServiceRequestRowUi(
    val id: String,
    val roomNumber: String,
    val title: String,
    val subtitle: String,
    val taskStatus: TaskStatus,
    val priority: TaskPriority,
    val action: TaskActionType,
    val source: RequestSource = RequestSource.ROOM,
    val iconUnicode: String? = null,
)

fun RoomRequest.toRowUi(priority: TaskPriority = TaskPriority.MEDIUM): ServiceRequestRowUi {
    val service = serviceDetails.firstOrNull()
    return ServiceRequestRowUi(
        id = id,
        roomNumber = roomId,
        title = displayTitle(),
        subtitle = status.toTaskSubtitle(createdDate),
        taskStatus = status.toTaskStatus(),
        priority = service?.priority?.toTaskPriority() ?: priority,
        action = status.toTaskAction(),
        source = RequestSource.ROOM,
        iconUnicode = iconUnicode?.takeIf { it.isNotBlank() }
            ?: service?.iconUnicode?.takeIf { it.isNotBlank() },
    )
}

fun OutletService.toRowUi(): ServiceRequestRowUi =
    ServiceRequestRowUi(
        id = id,
        roomNumber = "Box $boxId",
        title = title,
        subtitle = information.ifBlank { actionBy }.ifBlank { "Outlet service" },
        taskStatus = status.toTaskStatus(),
        priority = priority.toTaskPriority(),
        action = TaskActionType.NONE,
        source = RequestSource.OUTLET,
        iconUnicode = iconUnicode?.takeIf { it.isNotBlank() },
    )

fun String.toTaskStatus(): TaskStatus = when (uppercase()) {
    "NEW" -> TaskStatus.NEW
    "ACCEPT", "ACCEPTED" -> TaskStatus.ACCEPT
    "ASSIGN", "ASSIGNED" -> TaskStatus.ASSIGN
    "START", "STARTED" -> TaskStatus.START
    "HOLD", "PAUSE", "PAUSED" -> TaskStatus.HOLD
    "END" -> TaskStatus.END
    "DONE" -> TaskStatus.DONE
    "CLOSE", "CLOSED" -> TaskStatus.CLOSE
    "REJECT", "REJECTED" -> TaskStatus.REJECT
    "REOPEN", "REOPENED" -> TaskStatus.REOPEN
    else -> TaskStatus.DEFAULT
}

fun TaskStatus.toLabel(): String = when (this) {
    TaskStatus.NEW -> "New"
    TaskStatus.ACCEPT -> "Accept"
    TaskStatus.ASSIGN -> "Assign"
    TaskStatus.START -> "start"
    TaskStatus.HOLD -> "Hold"
    TaskStatus.END -> "End"
    TaskStatus.DONE -> "Done"
    TaskStatus.CLOSE -> "Closed"
    TaskStatus.REJECT -> "Reject"
    TaskStatus.REOPEN -> "Reopen"
    TaskStatus.DEFAULT -> ""
}

fun String.toTaskPriority(): TaskPriority = when (lowercase()) {
    "high" -> TaskPriority.HIGH
    "low" -> TaskPriority.LOW
    else -> TaskPriority.MEDIUM
}

fun RoomRequest.displayTitle(): String {
    val serviceTitle = serviceDetails.firstOrNull()?.title.orEmpty()
    return serviceTitle.ifBlank { title }
        .ifBlank { note.trim().lineSequence().firstOrNull().orEmpty() }
        .ifBlank { "Service Request" }
}

fun String.toTaskAction(): TaskActionType = when (uppercase()) {
    "NEW" -> TaskActionType.ACCEPT
    "ACCEPT", "ACCEPTED" -> TaskActionType.START
    else -> TaskActionType.NONE
}

fun String.toTaskSubtitle(createdDate: String): String = when (uppercase()) {
    "ACCEPT", "ACCEPTED" -> "Accepted · Ready to start"
    "START", "STARTED" -> "In progress"
    else -> createdDate.toDisplayTime()
}

fun String.toDisplayTime(): String {
    val (hour, minute) = parseTimeOfDay() ?: return legacyDisplayTime()
    return "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
}

fun String.toMinutesOfDay(): Int? {
    val (hour, minute) = parseTimeOfDay() ?: return null
    return hour * 60 + minute
}

private fun String.legacyDisplayTime(): String {
    val timePart = substringAfter(" ", "")
    return if (timePart.length >= 5) timePart.take(5) else this
}

private fun String.parseTimeOfDay(): Pair<Int, Int>? {
    val trimmed = trim()
    val isPm = trimmed.contains("PM", ignoreCase = true)
    val isAm = trimmed.contains("AM", ignoreCase = true)

    val normalized = trimmed
        .replace(Regex("(?i)\\s*AM"), "")
        .replace(Regex("(?i)\\s*PM"), "")
        .trim()

    val timePart = normalized.substringAfter(" ", normalized)
    val parts = timePart.split(":")
    if (parts.size < 2) return null

    var hour = parts[0].trim().toIntOrNull() ?: return null
    val minute = parts[1].trim().toIntOrNull() ?: return null

    if (isPm && hour in 1..11) hour += 12
    if (isAm && hour == 12) hour = 0

    if (hour !in 0..23 || minute !in 0..59) return null
    return hour to minute
}

fun RoomRequest.isActiveTask(): Boolean = status.uppercase() !in setOf("CLOSE", "CLOSED")

fun List<RoomRequest>.toActiveTaskRows(
    outletServices: List<OutletService> = emptyList(),
): List<ServiceRequestRowUi> {
    val priorityByServiceId = outletServices.associateBy({ it.serviceId }, { it.priority.toTaskPriority() })
    return filter { it.isActiveTask() }
        .map { it.toRowUi(priorityByServiceId[it.serviceId] ?: TaskPriority.MEDIUM) }
}

fun List<RoomRequest>.toTaskRows(
    outletServices: List<OutletService> = emptyList(),
): List<ServiceRequestRowUi> {
    val priorityByServiceId = outletServices.associateBy({ it.serviceId }, { it.priority.toTaskPriority() })
    return map { it.toRowUi(priorityByServiceId[it.serviceId] ?: TaskPriority.MEDIUM) }
}

fun mergeTaskRows(
    roomRequests: List<RoomRequest>,
    outletServices: List<OutletService>,
    includeOutletServices: Boolean = true,
    activeOnly: Boolean = false,
): List<ServiceRequestRowUi> {
    val roomRows = if (activeOnly) {
        roomRequests.toActiveTaskRows(outletServices)
    } else {
        roomRequests.toTaskRows(outletServices)
    }
    val outletRows = if (includeOutletServices) {
        outletServices.map { it.toRowUi() }
    } else {
        emptyList()
    }
    return roomRows + outletRows
}
