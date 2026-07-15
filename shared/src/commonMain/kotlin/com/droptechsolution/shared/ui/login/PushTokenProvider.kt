package com.droptechsolution.shared.ui.login

interface IPushTokenProvider {
    /** OneSignal player ID (push subscription id) for [StaffLoginRequest.deviceId]. */
    suspend fun requestToken(): String?
}

object NoOpPushTokenProvider : IPushTokenProvider {
    override suspend fun requestToken(): String? = null
}