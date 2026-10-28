package com.example.myapplication.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class UserPreferencesRepository(private val context: Context) {

    private val KEY_USER_NAME = stringPreferencesKey("user_name")
    private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
    private val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")
    private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    private val KEY_SELECTED_GOAL = stringPreferencesKey("selected_goal")
    private val KEY_IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")

    val userName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_NAME] ?: "BulbaLover"
    }

    val userEmail: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_EMAIL] ?: "expert@bulbascan.by"
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_IS_LOGGED_IN] ?: false
    }

    val selectedGoal: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_SELECTED_GOAL] ?: "Драники"
    }

    val isDarkTheme: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_IS_DARK_THEME] ?: true
    }

    suspend fun saveAuthSession(name: String, email: String, token: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_NAME] = name
            prefs[KEY_USER_EMAIL] = email
            prefs[KEY_AUTH_TOKEN] = token
            prefs[KEY_IS_LOGGED_IN] = true
        }
    }

    suspend fun clearAuthSession() {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_LOGGED_IN] = false
            prefs[KEY_AUTH_TOKEN] = ""
        }
    }

    suspend fun saveUserProfile(name: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_NAME] = name
            prefs[KEY_USER_EMAIL] = email
        }
    }

    suspend fun saveSelectedGoal(goal: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SELECTED_GOAL] = goal
        }
    }

    suspend fun setDarkTheme(isDark: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_DARK_THEME] = isDark
        }
    }
}
