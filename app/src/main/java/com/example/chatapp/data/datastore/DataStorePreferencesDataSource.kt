package com.example.chatapp.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStorePreferencesDataSource @Inject constructor(
    private val preferences: DataStore<Preferences>
) {

    private val accessTokenKey = stringPreferencesKey("access_token")

    val accessTokenFlow: Flow<String?> = preferences.data.map {
        it[accessTokenKey]
    }

    suspend fun saveAccessToken(token: String) {
        preferences.edit {
            it[accessTokenKey] = token
        }
    }

    suspend fun clear() {
        preferences.edit {
            it.clear()
        }
    }
}