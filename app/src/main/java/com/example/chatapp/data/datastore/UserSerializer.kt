package com.example.chatapp.data.datastore

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class UserSerializer @Inject constructor() : Serializer<CurrentUser> {
    override val defaultValue: CurrentUser = CurrentUser()

    override suspend fun readFrom(input: InputStream): CurrentUser {
        return try {
            Json.decodeFromString(
                CurrentUser.serializer(), input.readBytes().decodeToString()
            )
        } catch (exception: SerializationException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: CurrentUser, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(CurrentUser.serializer(), t)
                    .encodeToByteArray()
            )
        }
    }
}

val Context.currentUserDataStore: DataStore<CurrentUser> by dataStore(
    fileName = "current_user.json",
    serializer = UserSerializer()
)