package com.droptechsolution.shared.push

/**
 * OneSignal is initialized in the iOS app entry point (AppDelegate / SwiftUI App).
 * APNs credentials must be configured in the OneSignal dashboard before push works on iOS.
 *
 * After adding the OneSignalXCFramework pod and running `pod install`, replace these stubs
 * with cocoapods interop calls (see project OneSignal setup docs).
 */
actual object OneSignalWrapper {
    actual fun initialize() {
        // Initialized in AppDelegate.application(_:didFinishLaunchingWithOptions:)
    }

    actual fun setExternalId(id: String) {
        // OneSignal.login(id)
    }

    actual fun removeExternalId() {
        // OneSignal.logout()
    }

    actual fun sendTag(key: String, value: String) {
        // OneSignal.getUser().addTagWithKey(key, value)
    }

    actual fun requestPermission() {
        // OneSignal.getNotifications().requestPermission({ _ -> }, fallbackToSettings = false)
    }
}
