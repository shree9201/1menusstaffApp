package com.droptechsolution.shared.common.di

import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(
            appModule,
        )

    }
}