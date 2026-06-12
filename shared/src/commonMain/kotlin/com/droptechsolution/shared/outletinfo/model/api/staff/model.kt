package com.droptechsolution.shared.outletinfo.model.api.staff

import kotlinx.serialization.Serializable

@Serializable
data class StaffLoginRequest(
    val outletId: String,
    val username: String,
    val password: String,
    val deviceId: String,
    val deviceType:String
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