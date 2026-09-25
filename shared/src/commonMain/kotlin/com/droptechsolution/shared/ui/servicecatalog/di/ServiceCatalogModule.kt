package com.droptechsolution.shared.ui.servicecatalog.di

import com.droptechsolution.shared.services.interactor.ServiceCatalogInteractor
import com.droptechsolution.shared.ui.servicecatalog.ServiceCatalogRefreshNotifier
import com.droptechsolution.shared.ui.servicecatalog.presenter.ServiceCatalogDetailsViewModel
import com.droptechsolution.shared.ui.servicecatalog.presenter.ServiceCatalogFormViewModel
import com.droptechsolution.shared.ui.servicecatalog.presenter.ServiceCatalogListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val serviceCatalogModule = module {
    single { ServiceCatalogRefreshNotifier() }
    single { ServiceCatalogInteractor(get()) }
    viewModel { ServiceCatalogListViewModel(get(), get(), get(), get()) }
    viewModel { ServiceCatalogDetailsViewModel(get(), get(), get(), get()) }
    viewModel { ServiceCatalogFormViewModel(get(), get(), get(), get()) }
}
