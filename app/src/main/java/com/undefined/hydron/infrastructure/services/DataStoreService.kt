package com.undefined.hydron.infrastructure.services

import android.content.Context
import android.widget.Toast
import androidx.datastore.preferences.core.*
import com.undefined.hydron.domain.interfaces.IDataStore
import com.undefined.hydron.infrastructure.services.extensions.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cryptoManager: CryptoManager
) : IDataStore {

    override suspend fun setDataString(key: String, value: String) {
        val preferencesKey = stringPreferencesKey(key)
        val encryptedValue = cryptoManager.encrypt(value)
        val encryptedValueString = android.util.Base64.encodeToString(encryptedValue, android.util.Base64.DEFAULT)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = encryptedValueString
        }
    }

    override suspend fun getDataString(key: String): String {
        return try {
            val preferencesKey = stringPreferencesKey(key)
            val encryptedValueString = context.dataStore.data.map {
                it[preferencesKey] ?: ""
            }.first()

            if (encryptedValueString.isNotEmpty()) {
                val encryptedValue = android.util.Base64.decode(encryptedValueString, android.util.Base64.DEFAULT)
                cryptoManager.decrypt(encryptedValue)
            } else ""
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            "error"
        }
    }

    override suspend fun setDataBoolean(key: String, value: Boolean) {
        val preferencesKey = booleanPreferencesKey(key)
        context.dataStore.edit { it[preferencesKey] = value }
    }

    override suspend fun getDataBoolean(key: String): Boolean {
        return try {
            val preferencesKey = booleanPreferencesKey(key)
            context.dataStore.data.map {
                it[preferencesKey] ?: false
            }.first()
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            false
        }
    }

    override suspend fun setDataInt(key: String, value: Int) {
        val preferencesKey = intPreferencesKey(key)
        context.dataStore.edit { it[preferencesKey] = value }
    }

    override suspend fun getDataInt(key: String): Int {
        return try {
            val preferencesKey = intPreferencesKey(key)
            context.dataStore.data.map {
                it[preferencesKey] ?: 0
            }.first()
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            0
        }
    }

    override suspend fun setDouble(key: String, value: Double) {
        val preferencesKey = doublePreferencesKey(key)
        context.dataStore.edit { it[preferencesKey] = value }
    }

    override suspend fun getDouble(key: String): Double {
        return try {
            val preferencesKey = doublePreferencesKey(key)
            context.dataStore.data.map {
                it[preferencesKey] ?: 0.0
            }.first()
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            0.0
        }
    }

    fun getDataBooleanFlow(key: String): Flow<Boolean> {
        val prefKey = booleanPreferencesKey(key)
        return context.dataStore.data.map { prefs ->
            prefs[prefKey] ?: false
        }
    }
}
