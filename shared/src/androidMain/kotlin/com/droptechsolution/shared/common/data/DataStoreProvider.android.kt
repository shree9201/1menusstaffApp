package com.droptechsolution.shared.common.data
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.droptechsolution.shared.services.sla.initAndroidSlaScheduler

private lateinit var context: Context

fun initAndroidDataStore(appContext: Context) {
    context = appContext
    initAndroidSlaScheduler(appContext)
}

actual object DataStoreProvider {

    actual fun create(): DataStore<Preferences> {

        return PreferenceDataStoreFactory.create(
            produceFile = {
                context.preferencesDataStoreFile("user.preferences_pb")
            }
        )
    }
}