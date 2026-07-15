package com.droptechsolution.shared.push

actual class PushTokenProvider actual constructor() {
    actual suspend fun getPushToken(): String? = OneSignalPushTokens.awaitPlayerId()
}
