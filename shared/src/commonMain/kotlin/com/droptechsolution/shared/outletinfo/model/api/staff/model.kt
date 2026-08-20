package com.droptechsolution.shared.outletinfo.model.api.staff

import kotlinx.serialization.Serializable

@Serializable
data class StaffLoginRequest(
    val outletId: String,
    val username: String,
    val password: String,
    val deviceId: String,
    val deviceType:String,
    val userType  :String
)

@Serializable
data class StaffListRequest(
    val outletId: String
)

@Serializable
data class NotificationRequest(
    val staffId: String,
    val title : String,
    val message : String
)

@Serializable
data class StaffLogoutRequest(
    val outletId: Int,
    val staffId: Int,
)

@Serializable
data class StaffLogoutResponse(
    val status: Boolean = false,
    val value: String? = null,
)