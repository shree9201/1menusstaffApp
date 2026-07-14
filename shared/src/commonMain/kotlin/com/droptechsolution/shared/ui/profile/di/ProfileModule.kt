package com.droptechsolution.shared.ui.profile.di

import com.droptechsolution.shared.ui.profile.presenter.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val profileModule = module {
    viewModel { ProfileViewModel(get(), get()) }
}
