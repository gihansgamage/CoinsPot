package com.gihansgamage.coinspot.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        val IS_ONBOARDING_COMPLETE = booleanPreferencesKey("is_onboarding_complete")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_COUNTRY = stringPreferencesKey("user_country")
        val USER_CURRENCY = stringPreferencesKey("user_currency")
        val CURRENCY_SYMBOL = stringPreferencesKey("currency_symbol")
    }

    val isOnboardingComplete: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_ONBOARDING_COMPLETE] ?: false
        }

    val userName: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[USER_NAME] ?: ""
        }

    val userCountry: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[USER_COUNTRY] ?: ""
        }

    val userCurrency: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[USER_CURRENCY] ?: "USD"
        }

    val currencySymbol: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[CURRENCY_SYMBOL] ?: "$"
        }

    suspend fun setOnboardingComplete(isComplete: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_ONBOARDING_COMPLETE] = isComplete
        }
    }

    suspend fun saveUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME] = name
        }
    }

    suspend fun saveCountryAndCurrency(country: String, currency: String, symbol: String) {
        dataStore.edit { preferences ->
            preferences[USER_COUNTRY] = country
            preferences[USER_CURRENCY] = currency
            preferences[CURRENCY_SYMBOL] = symbol
        }
    }

    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}