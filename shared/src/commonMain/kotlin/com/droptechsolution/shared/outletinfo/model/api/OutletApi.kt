package com.droptechsolution.shared.outletinfo.model.api

import com.droptechsolution.shared.network.HttpClientProvider
import com.droptechsolution.shared.network.URN
import com.droptechsolution.shared.outletinfo.model.api.staff.NotificationRequest
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffLoginRequest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*

class OutletApi {

    suspend fun getOutletInfo(
        request: OutletRequest
    ): List<OutletResponse> {
        return HttpClientProvider.client.post(
            "https://1menus.com/app/API/outletInfo"
        ) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun staffLogin(request: StaffLoginRequest): StaffDetailsResponse {
        val response = HttpClientProvider.client.post(URN.SERVER+""+URN.STAFF_LOGIN)
        {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        println(request)
        val textResponse = response
        var text_response = textResponse.bodyAsText()
        println(text_response)
        return textResponse.body()
    }

    suspend fun sendNotification(request: NotificationRequest): String {
        val response = HttpClientProvider.client.post(URN.SERVER+"/"+URN.SEND_NOTIFICATION)
        {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        val valResponse = response
        val responseValue = valResponse.bodyAsText()
        println(response)
        return responseValue
    }

}