package com.droptechsolution.shared.common.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

actual object DataStoreProvider {
    actual fun create(): DataStore<Preferences> {
        TODO("Not yet implemented")
    }
}