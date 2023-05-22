package org.d3if3155.MoMi.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val PREFERENCES_NAME = "preferences"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PREFERENCES_NAME
)

class SettingDataStore(prefDataStore: DataStore<Preferences>) {

    private val IS_FIRST_TIME = booleanPreferencesKey("is_starter_finish")
    private val USER_ID_KEY = longPreferencesKey("user_id")

    val isFirstTime: Flow<Boolean> = prefDataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[IS_FIRST_TIME] ?: true }

    val userIdFlow: Flow<Long?> = prefDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[USER_ID_KEY] ?: 0
        }

    suspend fun saveFirstTime(isStarterFinish: Boolean, context: Context) {
        context.dataStore.edit { it[IS_FIRST_TIME] = isStarterFinish }
    }

    suspend fun saveUserId(userId: Long, context: Context) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
        }
    }
}
