package com.droptechsolution.menus

import android.app.Application
import com.droptechsolution.menus.common.di.AppComponent
import com.droptechsolution.menus.common.di.ApplicationModule
import com.droptechsolution.menus.common.di.DaggerAppComponent
import com.droptechsolution.shared.common.data.initAndroidDataStore
import com.droptechsolution.shared.common.di.initKoin

class MenusApplication : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        initAndroidDataStore(this)
        initKoin()

        appComponent = DaggerAppComponent.factory()
            .create(ApplicationModule(this))
        appComponent.inject(this)
    }

}