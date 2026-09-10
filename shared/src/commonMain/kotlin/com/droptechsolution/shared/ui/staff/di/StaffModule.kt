package com.droptechsolution.shared.ui.staff.di

import com.droptechsolution.shared.ui.staff.StaffListRefreshNotifier
import com.droptechsolution.shared.ui.staff.interactor.StaffInteractor
import com.droptechsolution.shared.ui.staff.presenter.StaffDetailsViewModel
import com.droptechsolution.shared.ui.staff.presenter.StaffFormViewModel
import com.droptechsolution.shared.ui.staff.presenter.StaffViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val staffModule = module {
    single { StaffListRefreshNotifier() }
    single { StaffInteractor(get()) }
    viewModel { StaffViewModel(get(), get(), get()) }
    viewModel { StaffDetailsViewModel(get(), get(), get()) }
    viewModel { StaffFormViewModel(get(), get(), get(), get()) }
}
