package com.droptechsolution.shared.masterdata.di

import com.droptechsolution.shared.masterdata.api.MasterDataApi
import com.droptechsolution.shared.masterdata.api.IMasterDataApi
import com.droptechsolution.shared.masterdata.interactor.MasterDataInteractor
import org.koin.dsl.bind
import org.koin.dsl.module

val masterDataModule = module {
    single { MasterDataApi(get()) } bind IMasterDataApi::class
    single { MasterDataInteractor(get(), get()) }
}
