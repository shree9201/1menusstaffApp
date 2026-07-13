package com.droptechsolution.shared.common.di

import com.droptechsolution.shared.services.sla.IosServiceSlaScheduler
import com.droptechsolution.shared.services.sla.ServiceSlaScheduler
import com.droptechsolution.shared.ui.login.IPushTokenProvider
import com.droptechsolution.shared.ui.login.NoOpPushTokenProvider
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<IPushTokenProvider> { NoOpPushTokenProvider }
    single<ServiceSlaScheduler> { IosServiceSlaScheduler() }
}
