package com.example.chatapp.data.repository.di

import com.example.chatapp.data.repository.AuthRepository
import com.example.chatapp.data.repository.AuthRepositoryImpl
import com.example.chatapp.data.repository.ChatSocketRepository
import com.example.chatapp.data.repository.ChatSocketRepositoryImpl
import com.example.chatapp.data.repository.ConversationRepository
import com.example.chatapp.data.repository.ConversationRepositoryImpl
import com.example.chatapp.data.repository.MessageRepository
import com.example.chatapp.data.repository.MessageRepositoryImpl
import com.example.chatapp.data.repository.UserRepository
import com.example.chatapp.data.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface RepositoryModule {

    @Binds
    fun bindAuthRepository(repository: AuthRepositoryImpl): AuthRepository

    @Binds
    fun bindConversationRepository(repository: ConversationRepositoryImpl): ConversationRepository

    @Binds
    fun bindMessageRepository(repository: MessageRepositoryImpl): MessageRepository

    @Binds
    fun bindChatSocketRepository(repository: ChatSocketRepositoryImpl): ChatSocketRepository

    @Binds
    fun bindUserRepository(repository: UserRepositoryImpl): UserRepository
}