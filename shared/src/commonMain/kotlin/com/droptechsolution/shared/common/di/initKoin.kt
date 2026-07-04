package com.droptechsolution.shared.common.di

import com.droptechsolution.shared.services.di.servicesModule
import com.droptechsolution.shared.ui.home.di.homeModule
import com.droptechsolution.shared.ui.login.di.loginModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module

fun initKoin(extraModules: List<Module> = emptyList()) {
    startKoin {
        modules(
            appModule,
            platformModule,
            homeModule,
            loginModule,
            servicesModule,
            *extraModules.toTypedArray(),
        )
    }
}