package com.example.chatapp.data.repository

import com.example.chatapp.model.Conversation
import com.example.chatapp.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class InMemoryLocalDataSource @Inject constructor() : LocalDataSource {

    private val conversationsListMutex = Mutex()
    private var conversationsList: MutableList<Conversation> = mutableListOf()
    override var conversationsListFlow: Flow<List<Conversation>> = emptyFlow()

    private val usersListMutex = Mutex()
    private var usersList: MutableList<User> = mutableListOf()
    override var usersListFlow: Flow<List<User>> = emptyFlow()

    override suspend fun saveConversationsList(conversations: List<Conversation>) {
        conversationsListMutex.withLock {
            conversationsList = conversations.toMutableList()
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

    override suspend fun saveUsersList(users: List<User>) {
        usersListMutex.withLock {
            usersList = users.toMutableList()
            usersListFlow = flowOf(users)
        }
    }

    override suspend fun saveUser(user: User) {
        usersListMutex.withLock {
            usersList.add(user)
            usersListFlow = flowOf(usersList)
        }
    }

    override suspend fun clear() {
        usersListMutex.withLock {
            usersList.clear()
        }

        conversationsListMutex.withLock {
            conversationsList.clear()
        }
    }
}