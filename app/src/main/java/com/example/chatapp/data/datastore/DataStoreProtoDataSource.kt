package com.example.chatapp.data.datastore

import androidx.datastore.core.DataStore
import com.example.chatapp.CurrentUser
import com.example.chatapp.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataStoreProtoDataSource @Inject constructor(
    private val currentUserProto: DataStore<CurrentUser>
) {

    val currentUser: Flow<CurrentUser>
        get() = currentUserProto.data

    suspend fun saveCurrentUser(user: User) {
        currentUserProto.updateData {
           it.toBuilder()
               .setId(user.id)
               .setFirstName(user.firstName)
               .setLastName(user.lastName)
               .setEmail(user.username)
               .setProfilePictureUrl(user.profilePictureUrl ?: "")
               .build()
        }
    }

    suspend fun clear() {
        currentUserProto.updateData {
            it.toBuilder().clear().build()
        }
    }
}