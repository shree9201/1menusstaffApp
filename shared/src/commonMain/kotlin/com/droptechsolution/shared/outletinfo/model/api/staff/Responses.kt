package com.droptechsolution.shared.outletinfo.model.api.staff

import kotlinx.serialization.Serializable

@Serializable
data class StaffDetails(
    val id : String,
    val title: String?,
    val userId: String,
    val tableIds: String,
    val name: String,
    val username: String,
    val password:String,
    val type: String,
    val department: String? = null,
    val customised_position: String?,
    val online: String,
    val status: String,
    val created_date: String,
    val updated_date: String
)

@Serializable
data class StaffResponse(
    val status: String,
    val value: List<StaffDetails>
)