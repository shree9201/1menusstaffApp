package com.droptechsolution.menus

import android.app.Application
import com.droptechsolution.menus.common.di.AppComponent
import com.droptechsolution.menus.common.di.ApplicationModule
import com.droptechsolution.menus.common.di.DaggerAppComponent
import com.droptechsolution.shared.common.data.initAndroidDataStore
import com.droptechsolution.shared.common.di.initKoin
import com.droptechsolution.shared.push.OneSignalConfig
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MenusApplication : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        initAndroidDataStore(this)
        initKoin()

        OneSignal.Debug.logLevel = LogLevel.VERBOSE
        OneSignal.initWithContext(this, OneSignalConfig.APP_ID)
        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(false)
        }

        appComponent = DaggerAppComponent.factory()
            .create(ApplicationModule(this))
        appComponent.inject(this)
    }
}
