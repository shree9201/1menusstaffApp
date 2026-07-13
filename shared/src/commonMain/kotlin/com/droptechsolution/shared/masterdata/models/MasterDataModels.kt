package com.droptechsolution.shared.masterdata.models

import com.droptechsolution.shared.outletinfo.model.api.staff.StaffDetails
import kotlinx.serialization.Serializable

@Serializable
object MasterDataRequest

@Serializable
data class MasterDataKeyValue(
    val key: String,
    val value: String,
)

@Serializable
data class MasterDataResponse(
    val status: String,
    val value: String,
    val staffTypes: List<MasterDataKeyValue> = emptyList(),
    val departments: List<MasterDataKeyValue> = emptyList(),
    val statusList: Map<String, String> = emptyMap(),
    val user: StaffDetails? = null,
)

data class MasterData(
    val staffTypes: List<MasterDataKeyValue>,
    val departments: List<MasterDataKeyValue>,
    val statusList: Map<String, String>,
    val user: StaffDetails?,
)

fun MasterDataResponse.isSuccessful(): Boolean =
    status.equals("true", ignoreCase = true) || status == "1"

fun MasterDataResponse.toDomain(): MasterData =
    MasterData(
        staffTypes = staffTypes,
        departments = departments,
        statusList = statusList,
        user = user,
    )
