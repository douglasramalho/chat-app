package com.example.chatapp.data.datastore

import com.example.chatapp.model.User
import kotlinx.coroutines.flow.Flow

interface DataStoreStorage {

    val currentUser: Flow<CurrentUser>

    suspend fun saveCurrentUser(currentUser: CurrentUser)
}