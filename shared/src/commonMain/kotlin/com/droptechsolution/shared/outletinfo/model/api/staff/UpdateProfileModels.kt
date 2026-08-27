package com.droptechsolution.shared.outletinfo.model.api.staff

import com.droptechsolution.shared.network.ApiStatusSerializer
import com.droptechsolution.shared.network.isApiSuccess
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class UpdateProfileRequest(
    val outletId: Int,
    val staffId: Int,
    val name: String,
    val email: String,
    val mobile: String,
    val username: String,
    val customised_position: String,
)

@Serializable
data class UpdateProfileResponse(
    @Serializable(with = ApiStatusSerializer::class)
    val status: String = "false",
    val value: String? = null,
)

fun UpdateProfileResponse.isSuccessful(): Boolean = status.isApiSuccess()

fun parseUpdateProfileResponse(jsonString: String): UpdateProfileResponse =
    updateProfileJson.decodeFromString<UpdateProfileResponse>(jsonString)

private val updateProfileJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}
