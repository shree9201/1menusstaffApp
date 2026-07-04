package com.droptechsolution.shared.services.models

enum class TaskPriority {
    HIGH,
    MEDIUM,
    LOW,
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
    val priority: TaskPriority,
    val action: TaskActionType,
)

fun RoomRequest.toRowUi(priority: TaskPriority = TaskPriority.MEDIUM): ServiceRequestRowUi {
    return ServiceRequestRowUi(
        id = id,
        roomNumber = roomId,
        title = displayTitle(),
        subtitle = status.toTaskSubtitle(createdDate),
        priority = priority,
        action = status.toTaskAction(),
    )
}

fun RoomRequest.displayTitle(): String =
    title.ifBlank { note.trim().lineSequence().firstOrNull().orEmpty() }
        .ifBlank { "Service Request" }

fun String.toTaskAction(): TaskActionType = when (uppercase()) {
    "NEW" -> TaskActionType.ACCEPT
    "ACCEPT" -> TaskActionType.START
    else -> TaskActionType.NONE
}

fun String.toTaskSubtitle(createdDate: String): String = when (uppercase()) {
    "ACCEPT" -> "Accepted · Ready to start"
    "START" -> "In progress"
    else -> createdDate.toDisplayTime()
}

fun String.toDisplayTime(): String {
    val timePart = substringAfter(" ", "")
    return if (timePart.length >= 5) timePart.take(5) else this
}

fun RoomRequest.isActiveTask(): Boolean = status.uppercase() != "CLOSE"

fun List<RoomRequest>.toActiveTaskRows(): List<ServiceRequestRowUi> =
    filter { it.isActiveTask() }.map { it.toRowUi() }
