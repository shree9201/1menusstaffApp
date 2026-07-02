package com.droptechsolution.shared.common.di

import androidx.datastore.core.DataStore
import com.droptechsolution.shared.common.data.DataStoreProvider
import com.droptechsolution.shared.network.APICall
import com.droptechsolution.shared.network.NetworkAPI
import com.droptechsolution.shared.ui.common.user.UserStorage
import com.droptechsolution.shared.ui.login.IPushTokenProvider
import com.droptechsolution.shared.ui.login.LoginViewModel
import com.droptechsolution.shared.ui.login.NoOpPushTokenProvider
import org.koin.dsl.module

val appModule = module {
    single {
        APICall()
    }
//
//    single<DataStore<Preferences>> {
//        createDataStore()
//    }
//
//    single {
//        PreferenceManager(get())
//    }

    single {
        DataStoreProvider.create()
    }

    single {
        UserStorage(get())
    }

    single<IPushTokenProvider> {
        NoOpPushTokenProvider
    }

    factory {
        LoginViewModel(
            pushTokenProvider = get(),
            userStorage = get()
        )
    }
}

private fun extracted() {
    //    single{
//        UserStorage()
//    }
}