package com.example.chatapp.data.repository

import com.example.chatapp.data.util.ResultStatus
import com.example.chatapp.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    suspend fun getMessages(receiverId: String): Flow<ResultStatus<List<Message>>>
}