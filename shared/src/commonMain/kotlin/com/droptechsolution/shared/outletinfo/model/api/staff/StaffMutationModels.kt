package com.droptechsolution.shared.outletinfo.model.api.staff

import com.droptechsolution.shared.network.ApiStatusSerializer
import com.droptechsolution.shared.network.isApiSuccess
import kotlinx.serialization.Serializable

@Serializable
data class AddStaffRequest(
    val outletId: Int,
    val staffId: Int,
    val name: String,
    val email: String,
    val mobile: String,
    val username: String,
    val password: String,
    val department: String,
    val type: String,
)

@Serializable
data class UpdateStaffRequest(
    val outletId: Int,
    val staffId: Int,
    val name: String,
    val email: String,
    val mobile: String,
    val username: String,
    val department: String,
    val type: String,
)

@Serializable
data class StaffMutationResponse(
    @Serializable(with = ApiStatusSerializer::class)
    val status: String = "false",
    val value: String? = null,
    val staffId: Int? = null,
)

fun StaffMutationResponse.isSuccessful(): Boolean = status.isApiSuccess()
