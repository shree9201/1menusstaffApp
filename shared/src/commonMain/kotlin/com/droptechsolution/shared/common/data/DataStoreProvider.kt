package com.droptechsolution.shared.common.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences


expect object DataStoreProvider {
    fun create(): DataStore<Preferences>
}