package com.droptechsolution.shared.push

import com.onesignal.OneSignal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

actual object OneSignalWrapper {
    actual fun initialize() {
        // Initialized in MenusApplication.onCreate()
    }

    actual fun setExternalId(id: String) {
        OneSignal.login(id)
    }

    actual fun removeExternalId() {
        OneSignal.logout()
    }

    actual fun sendTag(key: String, value: String) {
        OneSignal.User.addTag(key, value)
    }

    actual fun requestPermission() {
        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(false)
        }
    }
}
