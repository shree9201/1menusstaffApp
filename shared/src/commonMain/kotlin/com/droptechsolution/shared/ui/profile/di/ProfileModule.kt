package com.droptechsolution.shared.ui.profile.di

import com.droptechsolution.shared.ui.profile.attendance.presenter.MyAttendanceViewModel
import com.droptechsolution.shared.ui.profile.changepassword.presenter.ChangePasswordViewModel
import com.droptechsolution.shared.ui.profile.updateprofile.presenter.UpdateProfileViewModel
import com.droptechsolution.shared.ui.profile.presenter.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val profileModule = module {
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { MyAttendanceViewModel(get(), get()) }
    viewModel { ChangePasswordViewModel(get(), get()) }
    viewModel { UpdateProfileViewModel(get(), get()) }
}
