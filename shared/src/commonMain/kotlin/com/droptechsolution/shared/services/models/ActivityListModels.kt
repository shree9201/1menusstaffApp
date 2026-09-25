package com.droptechsolution.shared.services.models

import com.droptechsolution.shared.network.ApiStatusSerializer
import com.droptechsolution.shared.network.isApiSuccess
import kotlinx.serialization.Serializable

@Serializable
data class ActivityListRequest(
    val outletId: Int,
    val staffId: Int,
)

@Serializable
data class ActivityListItemDto(
    val id: String,
    val roomId: String? = null,
    val roomTitle: String? = null,
    val userId: String? = null,
    val userName: String? = null,
    val dateTime: String? = null,
    val status: String? = null,
    val comment: String? = null,
    val created_date: String? = null,
)

@Serializable
data class ActivityListResponse(
    @Serializable(with = ApiStatusSerializer::class)
    val status: String = "false",
    val value: String? = null,
    val count: Int = 0,
    val activityList: List<ActivityListItemDto> = emptyList(),
)

fun ActivityListResponse.isSuccessful(): Boolean = status.isApiSuccess()

data class ActivityListItemUi(
    val id: String,
    val comment: String,
    val roomTitle: String,
    val roomId: String,
    val userName: String,
    val dateTime: String,
    val status: String,
    val statusDisplay: RequestStatusDisplay,
)

fun ActivityListItemDto.toUi(): ActivityListItemUi {
    val rawStatus = status.orEmpty()
    return ActivityListItemUi(
        id = id,
        comment = comment.orEmpty().ifBlank { "Activity update" },
        roomTitle = roomTitle.orEmpty().ifBlank { roomId.orEmpty() }.ifBlank { "—" },
        roomId = roomId.orEmpty().ifBlank { "—" },
        userName = userName.orEmpty().ifBlank { "—" },
        dateTime = dateTime.orEmpty().toDisplayTime(),
        status = rawStatus,
        statusDisplay = rawStatus.toStatusDisplay(),
    )
}
