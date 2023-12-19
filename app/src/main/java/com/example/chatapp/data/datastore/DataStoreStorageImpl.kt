package com.example.chatapp.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataStoreStorageImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DataStoreStorage {

    private val Context.currentUserDataStore: DataStore<CurrentUser> by dataStore(
        fileName = "current_user.json",
        serializer = UserSerializer()
    )

    override val currentUser: Flow<CurrentUser>
        get() = context.currentUserDataStore.data

    override suspend fun saveCurrentUser(currentUser: CurrentUser) {
        context.currentUserDataStore.updateData {
            it.copy(
                id = currentUser.id,
                firstName = currentUser.firstName,
                lastName = currentUser.lastName,
                email = currentUser.email,
                profilePictureUrl = currentUser.profilePictureUrl
            )
        }
    }
}