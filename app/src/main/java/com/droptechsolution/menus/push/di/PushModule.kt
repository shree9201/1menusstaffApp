package com.droptechsolution.menus.push.di

import android.content.Context
import com.droptechsolution.menus.push.ITokenService
import com.droptechsolution.menus.push.TokenStoreService
import dagger.Module
import dagger.Provides

@Module
class PushModule {
    @Provides
    fun provideTokenService(context: Context): ITokenService {
        return TokenStoreService(context)
    }
}
