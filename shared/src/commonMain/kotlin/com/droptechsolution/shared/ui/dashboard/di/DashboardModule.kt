package com.droptechsolution.shared.ui.dashboard.di

import com.droptechsolution.shared.ui.dashboard.presenter.DashboardViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val dashboardModule = module {
    viewModel { DashboardViewModel(get(), get()) }
}
