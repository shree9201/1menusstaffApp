package com.droptechsolution.shared.services.models

import com.droptechsolution.shared.network.ApiStatusSerializer
import com.droptechsolution.shared.network.isApiSuccess
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffDetails
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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
data class NotificationDetailsDto(
    @Serializable(with = ApiStatusSerializer::class)
    val status: String = "false",
    val value: String? = null,
    val sent: Int = 0,
    val failed: Int = 0,
    val totalDevices: Int = 0,
)

@Serializable
data class UpdateRequestResponse(
    @Serializable(with = ApiStatusSerializer::class)
    val status: String = "true",
    val value: String? = null,
    val requestDetails: RequestDetailsResponse? = null,
    val notificationDetails: NotificationDetailsDto? = null,
)

fun UpdateRequestResponse.isSuccessful(): Boolean = status.isApiSuccess()

fun parseUpdateRequestResponse(jsonString: String): UpdateRequestResponse {
    return runCatching {
        networkJson.decodeFromString<UpdateRequestResponse>(jsonString)
    }.getOrElse { primaryError ->
        runCatching {
            val details = networkJson.decodeFromString<RequestDetailsResponse>(jsonString)
            UpdateRequestResponse(
                status = details.status,
                requestDetails = details,
            )
        }.getOrElse {
            throw primaryError
        }
    }
}

private val networkJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

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
