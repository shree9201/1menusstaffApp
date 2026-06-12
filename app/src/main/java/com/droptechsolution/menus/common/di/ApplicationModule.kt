package com.droptechsolution.menus.common.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val app: Application) {

    @Provides
    @Singleton
    fun provideApplication(): Application = app

    @Provides
    fun provideApplicationContext(): Context = app.applicationContext
//
//    @Provides
//    fun provideApplicationContext(application: Application): Context {
//        return application
//    }


}