package com.example.chatapp.data.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.chatapp.CurrentUser
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class CurrentUserSerializer @Inject constructor() : Serializer<CurrentUser> {
    override val defaultValue: CurrentUser
        get() = CurrentUser.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): CurrentUser {
        try {
            return CurrentUser.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: CurrentUser, output: OutputStream) {
        t.writeTo(output)
    }
}