package com.droptechsolution.shared.outletinfo.model.api.staff

import com.droptechsolution.shared.network.ApiStatusSerializer
import com.droptechsolution.shared.network.isApiSuccess
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ChangePasswordRequest(
    val outletId: Int,
    val staffId: Int,
    val oldPassword: String,
    val newPassword: String,
)

@Serializable
data class ChangePasswordResponse(
    @Serializable(with = ApiStatusSerializer::class)
    val status: String = "false",
    val value: String? = null,
)

fun ChangePasswordResponse.isSuccessful(): Boolean = status.isApiSuccess()

fun parseChangePasswordResponse(jsonString: String): ChangePasswordResponse =
    changePasswordJson.decodeFromString<ChangePasswordResponse>(jsonString)

private val changePasswordJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}
