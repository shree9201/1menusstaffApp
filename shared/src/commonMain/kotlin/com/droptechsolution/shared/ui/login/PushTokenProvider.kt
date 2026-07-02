package com.droptechsolution.shared.ui.login

interface IPushTokenProvider {
    suspend fun requestToken(): String?
}

object NoOpPushTokenProvider : IPushTokenProvider {
    override suspend fun requestToken(): String? = null
}

fun getToken(){}