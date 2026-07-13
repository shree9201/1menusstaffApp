package com.droptechsolution.shared.ui.login.di

import com.droptechsolution.shared.ui.login.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val loginModule = module {
    viewModel {
        LoginViewModel(
            pushTokenProvider = get(),
            userStorage = get(),
            outletApi = get(),
            masterDataInteractor = get(),
        )
    }
}
