package com.droptechsolution.shared.services.di

import com.droptechsolution.shared.services.interactor.ServicesInteractor
import com.droptechsolution.shared.services.services.IServicesAPI
import com.droptechsolution.shared.services.services.ServicesAPI
import org.koin.dsl.bind
import org.koin.dsl.module

val servicesModule = module {
    single { ServicesAPI(get()) } bind IServicesAPI::class
    single { ServicesInteractor(get()) }
}
