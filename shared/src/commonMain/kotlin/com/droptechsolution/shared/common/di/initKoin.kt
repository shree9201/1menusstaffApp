package com.droptechsolution.shared.common.di

import com.droptechsolution.shared.masterdata.di.masterDataModule
import com.droptechsolution.shared.services.di.servicesModule
import com.droptechsolution.shared.services.di.slaModule
import com.droptechsolution.shared.ui.dashboard.di.dashboardModule
import com.droptechsolution.shared.ui.home.di.homeModule
import com.droptechsolution.shared.ui.login.di.loginModule
import com.droptechsolution.shared.ui.profile.di.profileModule
import com.droptechsolution.shared.ui.staff.di.staffModule
import com.droptechsolution.shared.ui.tasks.di.tasksModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module

fun initKoin(extraModules: List<Module> = emptyList()) {
    startKoin {
        modules(
            appModule,
            platformModule,
            homeModule,
            dashboardModule,
            loginModule,
            servicesModule,
            slaModule,
            masterDataModule,
            tasksModule,
            staffModule,
            profileModule,
            *extraModules.toTypedArray(),
        )
    }
}