package com.droptechsolution.shared.network

import com.droptechsolution.shared.network.models.RequestData
import io.ktor.client.HttpClient

class APICall : NetworkAPI {
    override fun get(){

    }

    override fun post(body: RequestData) {
        TODO("Not yet implemented")
    }

    fun execute(){

    }

}


interface NetworkAPI{
    fun get()
    fun post(body:RequestData)

}