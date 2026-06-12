package com.droptechsolution.menus.common.di

import android.app.Application
import com.droptechsolution.menus.home.di.HomeComponent
import com.droptechsolution.menus.home.di.HomeModule
import com.droptechsolution.menus.push.di.PushComponent
import com.droptechsolution.menus.push.di.PushModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(
    modules = [
        ApplicationModule::class,
      PushModule::class,
        HomeModule::class
    ]
)

interface AppComponent {

    fun inject(app: Application)

    fun homeComponent(): HomeComponent.Factory
    fun pushComponent() : PushComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(appModule: ApplicationModule ): AppComponent
    }

}