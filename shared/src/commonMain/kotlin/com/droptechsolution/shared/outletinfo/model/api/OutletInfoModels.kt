package com.droptechsolution.shared.outletinfo.model.api

import com.droptechsolution.shared.network.models.RequestData
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffDetails
import kotlinx.serialization.Serializable

class OutletInfoModels {
}


data class OutletInfoRequest(val name : String) : RequestData {
    val city : String?=null
}

@Serializable
data class OutletRequest(
    val name: String,
    val city: String
)

@Serializable
data class AccountInfo(
    val id: String,
    val token: String,
    val title: String,
    val email: String,
    val mobile: String,
    val city: String,
    val address: String,
    val logo: String? = null,
    val cover_photo: String? = null,
    val about: String? = null
)

@Serializable
data class OutletHour(
    val day: String,
    val isOpen: Boolean,
    val open: String,
    val close: String,
    val label: String,
    val isBold: Boolean = false
)

@Serializable
data class MenuGroup(
    val id: String,
    val title: String,
    val img: String? = null,
    val photo: String? = null
)

@Serializable
data class SubscriptionInfo(
    val id: String,
    val userId: String,
    val start_date: String,
    val end_date: String,
    val package_status: String
)

@Serializable
data class PackageInfo(
    val id: String,
    val title: String,
    val price: String,
    val status: String
)

@Serializable
data class OutletResponse(
    val status: Boolean,
    val value: StaffDetails?,
    val accountInfo: AccountInfo? = null,
    val outlet_hours: List<OutletHour> = emptyList(),
    val menuGroup: List<MenuGroup> = emptyList(),

    val subscriptionInfo: SubscriptionInfo? = null,
    val packageInfo: PackageInfo? = null
)


@Serializable
data class StaffDetailsResponse(
    val status: Boolean,
    val value: StaffDetails,
    val devices: List<DeviceInfo> = emptyList(),
)

@Serializable
data class DeviceInfo(
    val deviceType: String? = null,
    val deviceId: String? = null,
    val last_login: String? = null
)