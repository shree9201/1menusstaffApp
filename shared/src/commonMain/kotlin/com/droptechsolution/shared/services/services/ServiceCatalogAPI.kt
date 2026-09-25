package com.droptechsolution.shared.services.services

import com.droptechsolution.shared.network.NetworkClient
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.network.URN
import com.droptechsolution.shared.services.models.DeleteCatalogServiceRequest
import com.droptechsolution.shared.services.models.ServiceCatalogListResponse
import com.droptechsolution.shared.services.models.ServiceCatalogRequest
import com.droptechsolution.shared.services.models.ServiceCategoryListResponse
import com.droptechsolution.shared.services.models.ServiceMutationResponse
import com.droptechsolution.shared.services.models.UpdateCatalogServiceRequest
import io.ktor.client.request.setBody

class ServiceCatalogAPI(
    private val networkClient: NetworkClient,
) {
    suspend fun viewServices(
        request: ServiceCatalogRequest,
    ): NetworkResult<ServiceCatalogListResponse> =
        networkClient.post("${URN.SERVER}${URN.SERVICE_VIEW}") {
            setBody(request)
        }

    suspend fun viewCategories(
        request: ServiceCatalogRequest,
    ): NetworkResult<ServiceCategoryListResponse> =
        networkClient.post("${URN.SERVER}${URN.SERVICE_CATEGORY}") {
            setBody(request)
        }

    suspend fun updateService(
        request: UpdateCatalogServiceRequest,
    ): NetworkResult<ServiceMutationResponse> =
        networkClient.post("${URN.SERVER}${URN.SERVICE_UPDATE}") {
            setBody(request)
        }

    suspend fun deleteService(
        request: DeleteCatalogServiceRequest,
    ): NetworkResult<ServiceMutationResponse> =
        networkClient.post("${URN.SERVER}${URN.SERVICE_DELETE}") {
            setBody(request)
        }
}
