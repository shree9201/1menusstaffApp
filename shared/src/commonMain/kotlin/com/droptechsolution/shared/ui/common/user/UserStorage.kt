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

//class UserStorage {
//    val Context.dataStore by preferencesDataStore(name = "token_prefs")
//}

class UserStorage(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun saveUser(name: String) {

        dataStore.edit {
            it[USERNAME] = name
        }

    }

    suspend fun saveStaffUser(name: StaffDetails) {
        dataStore.edit {
            it[LOGGED_IN_STAFF] = Json.encodeToString(name)
        }
    }

    suspend fun saveOutletId(outletId: String) {
        dataStore.edit {
            it[OUTLET_ID] = outletId
        }
    }

    fun getOutletId(): Flow<String?> =
        dataStore.data.map { preferences ->
            preferences[OUTLET_ID]?.takeIf { it.isNotBlank() }
        }

    suspend fun requireOutletId(): String =
        getOutletId().firstOrNull().orEmpty()

    fun getLoggedInStaff(): Flow<StaffDetails?> {
        return dataStore.data.map { preferences ->
            val jsonString = preferences[LOGGED_IN_STAFF]
            if (!jsonString.isNullOrEmpty()) {
                try {
                    Json.decodeFromString<StaffDetails>(jsonString)
                } catch (e: Exception) {
                    null // Returns null if JSON parsing fails
                }
            } else {
                null // Returns null if no staff is logged in
            }
        }
    }

    val user = dataStore.data.map {
        it[USERNAME] ?: ""
    }


    companion object {
        val USERNAME =
            stringPreferencesKey("username")
        val LOGGED_IN_STAFF =
            stringPreferencesKey("logged_in_staff")
        val OUTLET_ID =
            stringPreferencesKey("outlet_id")
    }
}