package com.droptechsolution.shared.ui.tasks.di

import com.droptechsolution.shared.ui.tasks.presenter.RequestDetailsViewModel
import com.droptechsolution.shared.ui.tasks.presenter.TasksViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val tasksModule = module {
    viewModel { TasksViewModel(get(), get()) }
    viewModel { RequestDetailsViewModel(get(), get(), get()) }
}
