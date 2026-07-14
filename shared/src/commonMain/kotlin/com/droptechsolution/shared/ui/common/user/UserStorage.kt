package com.droptechsolution.shared.ui.common.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class UserStorage(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun saveUser(name: String) {
        dataStore.edit {
            it[USERNAME] = name
        }
    }

    suspend fun saveStaffUser(staff: StaffDetails) {
        dataStore.edit {
            it[LOGGED_IN_STAFF] = Json.encodeToString(staff)
        }
    }

    /** Outlet ID comes from the logged-in staff record (`userId`). */
    fun getOutletId(): Flow<String?> =
        getLoggedInStaff().map { staff ->
            staff?.userId?.takeIf { it.isNotBlank() }
        }

    suspend fun requireOutletId(): String =
        getLoggedInStaff().firstOrNull()?.userId.orEmpty()

    fun getLoggedInStaff(): Flow<StaffDetails?> =
        dataStore.data.map { preferences ->
            val jsonString = preferences[LOGGED_IN_STAFF]
            if (!jsonString.isNullOrEmpty()) {
                try {
                    Json.decodeFromString<StaffDetails>(jsonString)
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }

    val user = dataStore.data.map {
        it[USERNAME] ?: ""
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(LOGGED_IN_STAFF)
            preferences.remove(USERNAME)
        }
    }

    companion object {
        val USERNAME = stringPreferencesKey("username")
        val LOGGED_IN_STAFF = stringPreferencesKey("logged_in_staff")
    }
}
