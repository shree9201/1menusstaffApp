package com.droptechsolution.shared.ui.home.di

import com.droptechsolution.shared.ui.common.user.UserStorage
import com.droptechsolution.shared.ui.home.presenter.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    viewModel {
        HomeViewModel(get(),
            get())
    }
}