package com.example.chatapp.data.repository

import com.example.chatapp.model.Conversation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class InMemoryLocalDataSource @Inject constructor() : LocalDataSource {

    private val conversationsListMutex = Mutex()
    private var conversationsList: List<Conversation> = emptyList()

    override var conversationsListFlow: Flow<List<Conversation>> = emptyFlow()

    override suspend fun saveConversationsList(conversations: List<Conversation>) {
        conversationsListMutex.withLock {
            conversationsList = conversations
            conversationsListFlow = flowOf(conversations)
        }
    }

    override suspend fun getConversationsList(): List<Conversation> {
        return conversationsListMutex.withLock { conversationsList }
    }

    override suspend fun getConversationBy(id: String): Conversation? {
        return conversationsListMutex.withLock {
            conversationsList.firstOrNull { it.id == id }
        }
    }
}