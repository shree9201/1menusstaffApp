package com.droptechsolution.shared.services.models

import com.droptechsolution.shared.network.ApiStatusSerializer
import com.droptechsolution.shared.network.isApiSuccess
import kotlinx.serialization.Serializable

@Serializable
data class ServiceCatalogRequest(
    val outletId: Int,
    val staffId: Int,
)

@Serializable
data class ServiceCatalogItemDto(
    val id: String,
    val title: String = "",
    val userId: String? = null,
    val boxId: String? = null,
    val serviceId: String? = null,
    val actionBy: String? = null,
    val aksDateTime: String? = null,
    val sq: String? = null,
    val information: String? = null,
    val reminderTime: String? = null,
    val escalationTime: String? = null,
    val priority: String? = null,
    val points: String? = null,
    val onHoldOption: String? = null,
    val status: String? = null,
    val created_date: String? = null,
    val updated_date: String? = null,
    val category: String? = null,
)

@Serializable
data class ServiceCatalogListResponse(
    @Serializable(with = ApiStatusSerializer::class)
    val status: String = "false",
    val value: String? = null,
    val count: Int = 0,
    val servicesList: List<ServiceCatalogItemDto> = emptyList(),
)

@Serializable
data class ServiceCategoryDto(
    val id: String,
    val title: String = "",
    val subTitle: String? = null,
    val userId: String? = null,
    val fId: String? = null,
    val sq: String? = null,
    val status: String? = null,
)

@Serializable
data class ServiceCategoryListResponse(
    @Serializable(with = ApiStatusSerializer::class)
    val status: String = "false",
    val value: String? = null,
    val count: Int = 0,
    val categoryList: List<ServiceCategoryDto> = emptyList(),
)

@Serializable
data class UpdateCatalogServiceRequest(
    val outletId: Int,
    val staffId: Int,
    val id: Int,
    val title: String,
    val boxId: String,
    val actionBy: String,
    val aksDateTime: String,
    val information: String,
    val reminderTime: Int,
    val escalationTime: Int,
    val priority: String,
    val points: Int,
    val onHoldOption: String,
    val status: String,
)

@Serializable
data class DeleteCatalogServiceRequest(
    val outletId: Int,
    val staffId: Int,
    val id: Int,
)

@Serializable
data class ServiceMutationResponse(
    @Serializable(with = ApiStatusSerializer::class)
    val status: String = "false",
    val value: String? = null,
    val id: Int? = null,
    val staffId: Int? = null,
    val updated: Boolean? = null,
)

fun ServiceCatalogListResponse.isSuccessful(): Boolean = status.isApiSuccess()

fun ServiceCategoryListResponse.isSuccessful(): Boolean = status.isApiSuccess()

fun ServiceMutationResponse.isSuccessful(): Boolean = status.isApiSuccess()

data class ServiceCatalogItemUi(
    val id: String,
    val title: String,
    val category: String,
    val actionBy: String,
    val actionByLabel: String,
    val information: String,
    val reminderTime: String,
    val escalationTime: String,
    val priority: String,
    val points: String,
    val onHoldOption: String,
    val status: String,
    val boxId: String,
    val aksDateTime: String,
    val serviceId: String,
)

fun ServiceCatalogItemDto.toUi(
    actionByLabel: String = actionBy.orEmpty(),
): ServiceCatalogItemUi = ServiceCatalogItemUi(
    id = id,
    title = title.ifBlank { "Untitled service" },
    category = category.orEmpty().ifBlank { "—" },
    actionBy = actionBy.orEmpty(),
    actionByLabel = actionByLabel.ifBlank { actionBy.orEmpty().ifBlank { "—" } },
    information = information.orEmpty(),
    reminderTime = reminderTime.orEmpty().ifBlank { "0" },
    escalationTime = escalationTime.orEmpty().ifBlank { "0" },
    priority = priority.orEmpty().ifBlank { "Medium" },
    points = points.orEmpty().ifBlank { "0" },
    onHoldOption = onHoldOption.orEmpty().ifBlank { "NO" },
    status = status.orEmpty().ifBlank { "NO" },
    boxId = boxId.orEmpty(),
    aksDateTime = aksDateTime.orEmpty().ifBlank { "NO" },
    serviceId = serviceId.orEmpty(),
)
