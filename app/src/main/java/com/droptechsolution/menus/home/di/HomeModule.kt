package com.droptechsolution.menus.home.di

import com.droptechsolution.menus.home.HomeViewModel
import com.droptechsolution.menus.home.HomeViewModelFactory
import com.droptechsolution.menus.push.ITokenService
import dagger.Module
import dagger.Provides

@Module
class HomeModule {

    @Provides
    fun provideHomeViewModel(tokenService: ITokenService) : HomeViewModel{
        return HomeViewModel(tokenService)
    }

    @Provides
    fun provideHomeViewModelFactory(tokenService: ITokenService) : HomeViewModelFactory {
        return HomeViewModelFactory(tokenService)
    }
}