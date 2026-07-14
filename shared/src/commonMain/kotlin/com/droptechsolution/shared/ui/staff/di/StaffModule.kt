package com.droptechsolution.shared.ui.staff.di

import com.droptechsolution.shared.ui.staff.interactor.StaffInteractor
import com.droptechsolution.shared.ui.staff.presenter.StaffViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val staffModule = module {
    single { StaffInteractor(get()) }
    viewModel { StaffViewModel(get(), get()) }
}
