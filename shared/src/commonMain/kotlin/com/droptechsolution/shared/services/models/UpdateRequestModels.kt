package com.droptechsolution.shared.services.models

import com.droptechsolution.shared.outletinfo.model.api.staff.StaffDetails
import kotlinx.serialization.Serializable

@Serializable
data class UpdateRequestBody(
    val outletId: String,
    val requestId: String,
    val status: String,
    val assignedTo: String,
    val comment: String,
    val user: StaffDetails,
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
    HOLD_ESCALATE,
    COMPLETE,
    REJECT,
}

fun RequestAction.apiStatus(): String = when (this) {
    RequestAction.ACCEPT -> "ACCEPTED"
    RequestAction.START -> "STARTED"
    RequestAction.PASS -> "PASS"
    RequestAction.PAUSE -> "HOLD"
    RequestAction.HOLD_ESCALATE -> "ESCALATED"
    RequestAction.COMPLETE -> "CLOSE"
    RequestAction.REJECT -> "REJECTED"
}

fun RequestAction.defaultComment(staffName: String): String = when (this) {
    RequestAction.ACCEPT -> "Request Accepted by $staffName"
    RequestAction.START -> "Work started by $staffName"
    RequestAction.PASS -> "Request passed by $staffName"
    RequestAction.PAUSE -> "Service paused by $staffName"
    RequestAction.HOLD_ESCALATE -> "Request held and escalated by $staffName"
    RequestAction.COMPLETE -> "Service completed by $staffName"
    RequestAction.REJECT -> "Request rejected by $staffName"
}
