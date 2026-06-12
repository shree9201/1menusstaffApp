package com.droptechsolution.menus.push.di

import android.content.Context
import com.droptechsolution.menus.push.ITokenService
import com.droptechsolution.menus.push.PushMessagingService
import com.droptechsolution.menus.push.TokenStoreService
import dagger.Module
import dagger.Provides

@Module
class PushModule {
    @Provides
    fun provideTokenService(context: Context,service : PushMessagingService): ITokenService {
        return TokenStoreService(context,service)
    }

    @Provides
    fun provideMessagingService() : PushMessagingService {
        return PushMessagingService()
    }
}