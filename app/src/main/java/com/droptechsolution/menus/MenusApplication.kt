package com.droptechsolution.menus

import android.app.Application
import com.droptechsolution.menus.common.di.AppComponent
import com.droptechsolution.menus.common.di.ApplicationModule
import com.droptechsolution.menus.common.di.DaggerAppComponent

class MenusApplication : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory()
            .create(ApplicationModule(this))
        appComponent.inject(this)
    }

}