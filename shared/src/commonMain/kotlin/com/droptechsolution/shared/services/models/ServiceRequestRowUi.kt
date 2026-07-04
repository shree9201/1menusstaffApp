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
    val source: RequestSource = RequestSource.ROOM,
)

fun RoomRequest.toRowUi(priority: TaskPriority = TaskPriority.MEDIUM): ServiceRequestRowUi {
    return ServiceRequestRowUi(
        id = id,
        roomNumber = roomId,
        title = displayTitle(),
        subtitle = status.toTaskSubtitle(createdDate),
        priority = priority,
        action = status.toTaskAction(),
        source = RequestSource.ROOM,
    )
}

fun OutletService.toRowUi(): ServiceRequestRowUi =
    ServiceRequestRowUi(
        id = id,
        roomNumber = "Box $boxId",
        title = title,
        subtitle = information.ifBlank { actionBy }.ifBlank { "Outlet service" },
        priority = priority.toTaskPriority(),
        action = TaskActionType.NONE,
        source = RequestSource.OUTLET,
    )

fun String.toTaskPriority(): TaskPriority = when (lowercase()) {
    "high" -> TaskPriority.HIGH
    "low" -> TaskPriority.LOW
    else -> TaskPriority.MEDIUM
}

fun RoomRequest.displayTitle(): String =
    title.ifBlank { note.trim().lineSequence().firstOrNull().orEmpty() }
        .ifBlank { "Service Request" }

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
    val timePart = substringAfter(" ", "")
    return if (timePart.length >= 5) timePart.take(5) else this
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
