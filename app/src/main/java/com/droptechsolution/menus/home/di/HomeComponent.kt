package com.droptechsolution.menus.home.di

import android.app.Application
import com.droptechsolution.menus.MainActivity
import com.droptechsolution.menus.common.di.AppComponent
import com.droptechsolution.menus.home.details.HomeActivity
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent(modules = [HomeModule::class])
interface HomeComponent {

    fun inject(activity: MainActivity)
    fun inject(activity: HomeActivity)

    @Subcomponent.Factory
    interface Factory {
        fun create(): HomeComponent
    }
}