package com.example.chatapp.data.remote.ws

import kotlinx.coroutines.flow.Flow

interface ChatSocketService {

    val isConnected: Boolean

    suspend fun openSession(userId: String)

    fun observeSocketResultFlow(): Flow<SocketSessionResult>

    suspend fun sendJsonStringData(
        dataJsonString: String
    )

    suspend fun disconnect()
}