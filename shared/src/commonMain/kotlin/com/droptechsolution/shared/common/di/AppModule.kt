package com.droptechsolution.shared.common.di

import com.droptechsolution.shared.common.data.DataStoreProvider
import com.droptechsolution.shared.network.NetworkClient
import com.droptechsolution.shared.outletinfo.model.api.OutletApi
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffAPI
import com.droptechsolution.shared.ui.common.user.UserStorage
import org.koin.core.module.Module
import org.koin.dsl.module

val appModule = module {
    single { NetworkClient() }
    single { OutletApi(get()) }
    single { StaffAPI(get()) }
    single { DataStoreProvider.create() }
    single { UserStorage(get()) }
}

expect val platformModule: Module
