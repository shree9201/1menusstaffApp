package com.droptechsolution.shared.outletinfo.model.api.staff

import com.droptechsolution.shared.network.HttpClientProvider
import com.droptechsolution.shared.network.URN
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class StaffAPI {
    suspend fun staffList(request: StaffListRequest): List<StaffResponse> {
        val response = HttpClientProvider.client.post(URN.SERVER+""+ URN.STAFF_LIST)
        {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        println("Received Staff List "+response)
            return response.body()
    }
}