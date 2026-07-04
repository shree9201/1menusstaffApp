package com.droptechsolution.shared.services.models

data class RequestDetailsUi(
    val id: String,
    val requestCode: String,
    val title: String,
    val roomNumber: String,
    val status: String,
    val statusLabel: String,
    val note: String,
    val guestName: String,
    val points: Int,
    val createdDate: String,
    val updatedDate: String,
    val startTime: String,
    val endTime: String,
    val action: TaskActionType,
    val activities: List<RequestActivityUi>,
)

data class RequestActivityUi(
    val dateTime: String,
    val status: String,
    val comment: String,
    val assignedTo: String,
)

fun RequestDetails.toUi(): RequestDetailsUi {
    val displayTitle = request.displayTitle()
    return RequestDetailsUi(
        id = request.id,
        requestCode = request.requestCode,
        title = displayTitle,
        roomNumber = request.roomId,
        status = request.status,
        statusLabel = request.status.toStatusLabel(),
        note = request.note,
        guestName = request.guestName,
        points = request.points,
        createdDate = request.createdDate,
        updatedDate = request.updatedDate,
        startTime = request.startTime,
        endTime = request.endTime,
        action = request.status.toTaskAction(),
        activities = activities.map { it.toUi() },
    )
}

fun RequestActivity.toUi(): RequestActivityUi =
    RequestActivityUi(
        dateTime = dateTime,
        status = status,
        comment = comment,
        assignedTo = assignedTo,
    )

fun OutletService.toDetailsUi(): RequestDetailsUi =
    RequestDetailsUi(
        id = id,
        requestCode = "SRV-$id",
        title = title,
        roomNumber = "Box $boxId",
        status = status,
        statusLabel = if (status.equals("YES", ignoreCase = true)) "Active" else "Inactive",
        note = information,
        guestName = "",
        points = points,
        createdDate = "",
        updatedDate = "",
        startTime = "",
        endTime = "",
        action = TaskActionType.NONE,
        activities = emptyList(),
    )

private fun String.toStatusLabel(): String = when (uppercase()) {
    "NEW" -> "New"
    "ACCEPT", "ACCEPTED" -> "Accepted"
    "START", "STARTED" -> "In Progress"
    "CLOSE", "CLOSED" -> "Closed"
    "ESCALATED" -> "Escalated"
    else -> this
}
