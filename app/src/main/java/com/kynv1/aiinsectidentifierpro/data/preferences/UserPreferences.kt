package com.kynv1.aiinsectidentifierpro.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val isPremiumKey = booleanPreferencesKey("is_premium")

    val isPremium: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[isPremiumKey] ?: false
    }

    suspend fun setPremium(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[isPremiumKey] = value
        }
    }
}
