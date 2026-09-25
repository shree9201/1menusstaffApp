package com.droptechsolution.shared.services.interactor

import com.droptechsolution.shared.network.NetworkError
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.services.models.DeleteCatalogServiceRequest
import com.droptechsolution.shared.services.models.ServiceCatalogItemUi
import com.droptechsolution.shared.services.models.ServiceCatalogRequest
import com.droptechsolution.shared.services.models.ServiceCategoryDto
import com.droptechsolution.shared.services.models.UpdateCatalogServiceRequest
import com.droptechsolution.shared.services.models.isSuccessful
import com.droptechsolution.shared.services.models.toUi
import com.droptechsolution.shared.services.services.ServiceCatalogAPI

class ServiceCatalogInteractor(
    private val serviceCatalogAPI: ServiceCatalogAPI,
) {
    suspend fun loadServices(
        outletId: String,
        staffId: String,
        actionByLabels: Map<String, String> = emptyMap(),
    ): NetworkResult<List<ServiceCatalogItemUi>> {
        val request = catalogRequest(outletId, staffId) ?: return invalidSession()
        return when (val result = serviceCatalogAPI.viewServices(request)) {
            is NetworkResult.Success -> {
                if (result.data.isSuccessful()) {
                    NetworkResult.Success(
                        result.data.servicesList.map { item ->
                            item.toUi(
                                actionByLabel = actionByLabels[item.actionBy.orEmpty()]
                                    ?: item.actionBy.orEmpty(),
                            )
                        },
                    )
                } else {
                    NetworkResult.Error(
                        NetworkError.Unknown(
                            result.data.value?.takeIf { it.isNotBlank() } ?: "Unable to load services",
                        ),
                    )
                }
            }
            is NetworkResult.Error -> result
        }
    }

    suspend fun loadCategories(
        outletId: String,
        staffId: String,
    ): NetworkResult<List<ServiceCategoryDto>> {
        val request = catalogRequest(outletId, staffId) ?: return invalidSession()
        return when (val result = serviceCatalogAPI.viewCategories(request)) {
            is NetworkResult.Success -> {
                if (result.data.isSuccessful()) {
                    NetworkResult.Success(result.data.categoryList)
                } else {
                    NetworkResult.Error(
                        NetworkError.Unknown(
                            result.data.value?.takeIf { it.isNotBlank() }
                                ?: "Unable to load service categories",
                        ),
                    )
                }
            }
            is NetworkResult.Error -> result
        }
    }

    suspend fun updateService(
        outletId: String,
        staffId: String,
        service: ServiceCatalogItemUi,
    ): NetworkResult<String> {
        val outletIdInt = outletId.toIntOrNull() ?: return invalidSession()
        val staffIdInt = staffId.toIntOrNull() ?: return invalidSession()
        val serviceIdInt = service.id.toIntOrNull()
            ?: return NetworkResult.Error(NetworkError.Unknown("Invalid service ID"))

        return when (
            val result = serviceCatalogAPI.updateService(
                UpdateCatalogServiceRequest(
                    outletId = outletIdInt,
                    staffId = staffIdInt,
                    id = serviceIdInt,
                    title = service.title.trim(),
                    boxId = service.boxId.trim(),
                    actionBy = service.actionBy.trim(),
                    aksDateTime = service.aksDateTime.trim().ifBlank { "NO" },
                    information = service.information.trim(),
                    reminderTime = service.reminderTime.toIntOrNull() ?: 0,
                    escalationTime = service.escalationTime.toIntOrNull() ?: 0,
                    priority = service.priority.trim().ifBlank { "Medium" },
                    points = service.points.toIntOrNull() ?: 0,
                    onHoldOption = service.onHoldOption.trim().ifBlank { "NO" },
                    status = service.status.trim().ifBlank { "YES" },
                ),
            )
        ) {
            is NetworkResult.Success -> mutationResult(result.data.isSuccessful(), result.data.value, "Service updated successfully", "Unable to update service")
            is NetworkResult.Error -> result
        }
    }

    suspend fun deleteService(
        outletId: String,
        staffId: String,
        serviceId: String,
    ): NetworkResult<String> {
        val outletIdInt = outletId.toIntOrNull() ?: return invalidSession()
        val staffIdInt = staffId.toIntOrNull() ?: return invalidSession()
        val serviceIdInt = serviceId.toIntOrNull()
            ?: return NetworkResult.Error(NetworkError.Unknown("Invalid service ID"))

        return when (
            val result = serviceCatalogAPI.deleteService(
                DeleteCatalogServiceRequest(
                    outletId = outletIdInt,
                    staffId = staffIdInt,
                    id = serviceIdInt,
                ),
            )
        ) {
            is NetworkResult.Success -> mutationResult(result.data.isSuccessful(), result.data.value, "Service deleted successfully", "Unable to delete service")
            is NetworkResult.Error -> result
        }
    }

    private fun catalogRequest(outletId: String, staffId: String): ServiceCatalogRequest? {
        val outletIdInt = outletId.toIntOrNull() ?: return null
        val staffIdInt = staffId.toIntOrNull() ?: return null
        return ServiceCatalogRequest(outletId = outletIdInt, staffId = staffIdInt)
    }

    private fun invalidSession(): NetworkResult.Error =
        NetworkResult.Error(NetworkError.Unknown("Outlet ID not found. Please log in again."))

    private fun mutationResult(
        successful: Boolean,
        value: String?,
        successFallback: String,
        errorFallback: String,
    ): NetworkResult<String> =
        if (successful) {
            NetworkResult.Success(value?.takeIf { it.isNotBlank() } ?: successFallback)
        } else {
            NetworkResult.Error(NetworkError.Unknown(value?.takeIf { it.isNotBlank() } ?: errorFallback))
        }
}
