package com.droptechsolution.shared.common.di

import com.droptechsolution.shared.push.PushTokenProvider
import com.droptechsolution.shared.services.sla.AndroidServiceSlaScheduler
import com.droptechsolution.shared.services.sla.ServiceSlaScheduler
import com.droptechsolution.shared.ui.login.IPushTokenProvider
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<IPushTokenProvider> {
        object : IPushTokenProvider {
            override suspend fun requestToken(): String? = PushTokenProvider().getPushToken()
        }
    }
    single<ServiceSlaScheduler> { AndroidServiceSlaScheduler() }
}
