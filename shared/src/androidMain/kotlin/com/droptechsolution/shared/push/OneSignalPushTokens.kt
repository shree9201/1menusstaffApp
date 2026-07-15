package com.droptechsolution.shared.push

import com.onesignal.OneSignal
import com.onesignal.user.subscriptions.IPushSubscriptionObserver
import com.onesignal.user.subscriptions.PushSubscriptionChangedState
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

internal object OneSignalPushTokens {
    /**
     * Returns the OneSignal push subscription ID (player_id in OneSignal dashboard/API).
     */
    suspend fun awaitPlayerId(timeoutMs: Long = 15_000): String? = withTimeoutOrNull(timeoutMs) {
        val current = OneSignal.User.pushSubscription.id
        if (!current.isNullOrBlank()) {
            return@withTimeoutOrNull current
        }

        suspendCancellableCoroutine { continuation ->
            val observer = object : IPushSubscriptionObserver {
                override fun onPushSubscriptionChange(state: PushSubscriptionChangedState) {
                    val playerId = state.current.id
                    if (!playerId.isNullOrBlank()) {
                        OneSignal.User.pushSubscription.removeObserver(this)
                        if (continuation.isActive) {
                            continuation.resume(playerId)
                        }
                    }
                }
            }
            OneSignal.User.pushSubscription.addObserver(observer)
            continuation.invokeOnCancellation {
                OneSignal.User.pushSubscription.removeObserver(observer)
            }
        }
    }
}
