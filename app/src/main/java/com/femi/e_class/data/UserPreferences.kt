package com.femi.e_class.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "USER_PREFERENCES")

class UserPreferences(val context: Context) {

    private object PreferencesKeys {
        val KEY_USER_EMAIL = stringPreferencesKey("KEY_USER_EMAIL")
        val KEY_USER_MATRIC = longPreferencesKey("KEY_USER_MATRIC")
        val KEY_USER_FNAME = stringPreferencesKey("KEY_USER_FNAME")
        val KEY_USER_LNAME = stringPreferencesKey("KEY_USER_LNAME")
        val KEY_VIDEO_RESOLUTION = intPreferencesKey("KEY_VIDEO_RESOLUTION")
    }

    val userEmail: Flow<String>
        get() = context.dataStore.data.map { preference ->
            preference[PreferencesKeys.KEY_USER_EMAIL] ?: ""
        }

    suspend fun userEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_USER_EMAIL] = email
        }
    }

    val userMatric: Flow<Long>
        get() = context.dataStore.data.map { preference ->
            preference[PreferencesKeys.KEY_USER_MATRIC] ?: 0L
        }

    suspend fun userMatric(matric: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_USER_MATRIC] = matric
        }
    }

    val userFName: Flow<String>
        get() = context.dataStore.data.map { preference ->
            preference[PreferencesKeys.KEY_USER_FNAME] ?: ""
        }

    suspend fun userFName(fName: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_USER_FNAME] = fName
        }
    }

    val userLName: Flow<String>
        get() = context.dataStore.data.map { preference ->
            preference[PreferencesKeys.KEY_USER_LNAME] ?: ""
        }

    suspend fun userLName(lName: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_USER_LNAME] = lName
        }
    }

    val videoResolution: Flow<Int>
        get() = context.dataStore.data.map { preference ->
            preference[PreferencesKeys.KEY_VIDEO_RESOLUTION] ?: 720
        }

    suspend fun videoResolution(resolution: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_VIDEO_RESOLUTION] = resolution
        }
    }

}