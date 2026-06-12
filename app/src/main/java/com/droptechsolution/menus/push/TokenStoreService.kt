package com.droptechsolution.menus.push

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TokenStoreService @Inject constructor(private val context: Context,
    private val pushService : PushMessagingService
    )  : ITokenService {
    val Context.dataStore by preferencesDataStore(name = "token_prefs")

    companion object {
        val PUSH_TOKEN = stringPreferencesKey("push_token")
    }

    private val _token = MutableStateFlow("")
     val token: StateFlow<String> = _token
    override suspend fun getPushToken(): String? {
        return context.dataStore.data.map { prefs -> prefs[PUSH_TOKEN] }.firstOrNull()
    }

    override suspend fun setPushToken(token:String) {
        context.dataStore.edit { prefs ->
            prefs[PUSH_TOKEN] = token
        }
    }

    override suspend fun requestToken(): String? {
        return pushService.getPushToken()
    }
}

interface ITokenService{
    suspend fun getPushToken() : String?
    suspend fun setPushToken(token: String)
    suspend fun requestToken():String?
}