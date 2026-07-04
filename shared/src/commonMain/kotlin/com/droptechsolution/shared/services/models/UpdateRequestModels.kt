package com.droptechsolution.shared.services.models

import kotlinx.serialization.Serializable

@Serializable
data class UpdateRequestBody(
    val outletId: String,
    val requestId: String,
    val status: String,
    val assignedTo: String,
    val comment: String,
)

@Serializable
data class UpdateRequestResponse(
    val status: String,
    val value: String? = null,
    val requestDetails: RequestDetailsResponse? = null,
)

fun UpdateRequestResponse.isSuccessful(): Boolean =
    status.equals("true", ignoreCase = true) || status == "1"

enum class RequestAction {
    ACCEPT,
    START,
    PASS,
    PAUSE,
    REJECT,
}

fun RequestAction.apiStatus(): String = when (this) {
    RequestAction.ACCEPT -> "ACCEPTED"
    RequestAction.START -> "STARTED"
    RequestAction.PASS -> "PASS"
    RequestAction.PAUSE -> "HOLD"
    RequestAction.REJECT -> "REJECTED"
}

fun RequestAction.defaultComment(staffName: String): String = when (this) {
    RequestAction.ACCEPT -> "Request Accepted by $staffName"
    RequestAction.START -> "Service started by $staffName"
    RequestAction.PASS -> "Request passed by $staffName"
    RequestAction.PAUSE -> "Request paused by $staffName"
    RequestAction.REJECT -> "Request rejected by $staffName"
}
