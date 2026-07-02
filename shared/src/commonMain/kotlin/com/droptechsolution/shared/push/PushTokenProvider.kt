package com.droptechsolution.shared.push

expect class PushTokenProvider() {
    suspend fun getPushToken(): String?
}