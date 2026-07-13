package com.droptechsolution.shared.services.di

import com.droptechsolution.shared.services.sla.ServiceSlaRepository
import com.droptechsolution.shared.services.sla.ServiceSlaScheduler
import com.droptechsolution.shared.services.sla.ServiceSlaTracker
import org.koin.dsl.module

val slaModule = module {
    single { ServiceSlaRepository(get()) }
    single { ServiceSlaTracker(get(), get()) }
}
