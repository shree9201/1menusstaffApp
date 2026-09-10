package com.droptechsolution.shared.outletinfo.model.api.staff

import com.droptechsolution.shared.network.ApiStatusSerializer
import com.droptechsolution.shared.network.isApiSuccess
import kotlinx.serialization.Serializable

@Serializable
data class DeleteStaffRequest(
    val outletId: Int,
    val staffId: Int,
)

@Serializable
data class DeleteStaffResponse(
    @Serializable(with = ApiStatusSerializer::class)
    val status: String = "false",
    val value: String? = null,
    val staffId: Int? = null,
)

fun DeleteStaffResponse.isSuccessful(): Boolean = status.isApiSuccess()
