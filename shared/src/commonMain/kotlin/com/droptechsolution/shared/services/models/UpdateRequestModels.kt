package com.droptechsolution.shared.services.models

import com.droptechsolution.shared.network.ApiStatusSerializer
import com.droptechsolution.shared.network.isApiSuccess
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffDetails
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

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

data class UpdateRequestResult(
    val message: String,
    val details: RequestDetailsUi? = null,
)

fun parseUpdateRequestResponse(jsonString: String): UpdateRequestResponse {
    val payloads = splitTopLevelJsonObjects(jsonString)
        .sortedByDescending { payload -> payload.contains("requestDetails") }
        .ifEmpty { listOf(jsonString.trim()) }

    var lastError: Throwable? = null
    for (payload in payloads) {
        try {
            return parseUpdateRequestPayload(payload)
        } catch (error: Throwable) {
            lastError = error
        }
    }

    throw lastError ?: IllegalArgumentException("Unable to parse updateRequest response")
}

private fun parseUpdateRequestPayload(payload: String): UpdateRequestResponse {
    val root = networkJson.parseToJsonElement(payload).jsonObject
    val status = root["status"]?.jsonPrimitive?.content ?: "false"
    val value = root["value"]?.jsonPrimitive?.content

    val requestDetails = root["requestDetails"]?.let { element ->
        runCatching {
            networkJson.decodeFromJsonElement(RequestDetailsResponse.serializer(), element)
        }.getOrNull()?.takeIf { it.isSuccessful() }
    }

    val notificationDetails = root["notificationDetails"]?.let { element ->
        runCatching {
            networkJson.decodeFromJsonElement(NotificationDetailsDto.serializer(), element)
        }.getOrNull()
    }

    return UpdateRequestResponse(
        status = status,
        value = value,
        requestDetails = requestDetails,
        notificationDetails = notificationDetails,
    )
}

/**
 * Some responses prepend unrelated JSON before the real payload, e.g.
 * `{"id":"...","external_id":null}{"status":"true",...}`.
 */
internal fun splitTopLevelJsonObjects(raw: String): List<String> {
    val trimmed = raw.trim()
    if (trimmed.isEmpty()) return emptyList()

    val objects = mutableListOf<String>()
    var depth = 0
    var start = -1
    var inString = false
    var escaped = false

    for (i in trimmed.indices) {
        val char = trimmed[i]
        if (inString) {
            when {
                escaped -> escaped = false
                char == '\\' -> escaped = true
                char == '"' -> inString = false
            }
            continue
        }

        when (char) {
            '"' -> inString = true
            '{' -> {
                if (depth == 0) start = i
                depth++
            }
            '}' -> {
                depth--
                if (depth == 0 && start >= 0) {
                    objects.add(trimmed.substring(start, i + 1))
                    start = -1
                }
            }
        }
    }

    return objects
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
