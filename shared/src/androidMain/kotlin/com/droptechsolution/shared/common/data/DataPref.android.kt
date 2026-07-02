package com.droptechsolution.shared.common.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile

actual fun createDataStore(): DataStore<Preferences> {
    TODO("Not yet implemented")
}


class AndroidDataStoreFactory(
    private val context: Context
) {
    fun create(): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = {
                context.preferencesDataStoreFile("user.preferences_pb")
            }
        )
}