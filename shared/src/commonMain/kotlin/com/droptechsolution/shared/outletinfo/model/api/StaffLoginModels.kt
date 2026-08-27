package com.droptechsolution.shared.outletinfo.model.api

import com.droptechsolution.shared.network.isApiSuccess
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffDetails
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

sealed interface StaffLoginResult {
    data class Success(
        val staff: StaffDetails,
        val devices: List<DeviceInfo> = emptyList(),
    ) : StaffLoginResult

    data class Failure(val message: String) : StaffLoginResult
}

fun parseStaffLoginResponse(jsonString: String): StaffLoginResult {
    val root = staffLoginJson.parseToJsonElement(jsonString).jsonObject
    val status = root.readStatus()
    val value = root["value"]

    if (!status.isApiSuccess()) {
        return StaffLoginResult.Failure(value.readErrorMessage())
    }

    if (value !is JsonObject) {
        return StaffLoginResult.Failure("Invalid credentials")
    }

    return try {
        StaffLoginResult.Success(
            staff = staffLoginJson.decodeFromJsonElement<StaffDetails>(value),
            devices = root["devices"]?.let { devices ->
                staffLoginJson.decodeFromJsonElement<List<DeviceInfo>>(devices)
            }.orEmpty(),
        )
    } catch (e: SerializationException) {
        throw e
    }
}

private fun JsonObject.readStatus(): String {
    val statusElement = this["status"] ?: return "false"
    val primitive = statusElement.jsonPrimitive
    return if (primitive.isString) {
        primitive.content
    } else {
        primitive.contentOrNull ?: "false"
    }
}

private fun JsonElement?.readErrorMessage(): String {
    val primitive = this?.jsonPrimitive
    return primitive?.contentOrNull?.takeIf { it.isNotBlank() } ?: "Invalid credentials"
}

private val staffLoginJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}
