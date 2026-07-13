package com.droptechsolution.shared.services.sla

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ServiceSlaRepository(
    private val dataStore: DataStore<Preferences>,
) {
    suspend fun getSession(): ServiceSlaSession? {
        val prefs = dataStore.data.first()
        val requestId = prefs[REQUEST_ID] ?: return null
        val reminderMinutes = prefs[REMINDER_MINUTES] ?: return null
        val startedAt = prefs[STARTED_AT] ?: return null
        val isRunning = prefs[IS_RUNNING] ?: false
        return ServiceSlaSession(
            requestId = requestId,
            reminderMinutes = reminderMinutes,
            startedAtEpochMs = startedAt,
            isRunning = isRunning,
        )
    }

    suspend fun saveSession(session: ServiceSlaSession) {
        dataStore.edit { prefs ->
            prefs[REQUEST_ID] = session.requestId
            prefs[REMINDER_MINUTES] = session.reminderMinutes
            prefs[STARTED_AT] = session.startedAtEpochMs
            prefs[IS_RUNNING] = session.isRunning
        }
    }

    suspend fun stopSession(requestId: String) {
        val current = getSession() ?: return
        if (current.requestId != requestId) return
        dataStore.edit { prefs ->
            prefs[IS_RUNNING] = false
        }
    }

    suspend fun clearSession(requestId: String) {
        val current = getSession() ?: return
        if (current.requestId != requestId) return
        dataStore.edit { prefs ->
            prefs.remove(REQUEST_ID)
            prefs.remove(REMINDER_MINUTES)
            prefs.remove(STARTED_AT)
            prefs.remove(IS_RUNNING)
        }
    }

    fun observeIsRunning(requestId: String) =
        dataStore.data.map { prefs ->
            prefs[REQUEST_ID] == requestId && prefs[IS_RUNNING] == true
        }

    companion object {
        private val REQUEST_ID = stringPreferencesKey("sla_request_id")
        private val REMINDER_MINUTES = intPreferencesKey("sla_reminder_minutes")
        private val STARTED_AT = longPreferencesKey("sla_started_at")
        private val IS_RUNNING = booleanPreferencesKey("sla_is_running")
    }
}
