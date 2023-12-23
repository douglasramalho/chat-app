package com.example.chatapp.data.di

import com.example.chatapp.data.ws.ChatSocketService
import com.example.chatapp.data.ws.ChatSocketServiceImpl
import com.example.chatapp.data.network.NetworkDataSource
import com.example.chatapp.data.network.NetworkDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    @Singleton
    fun bindRemoteDataSource(remoteDataSource: NetworkDataSourceImpl): NetworkDataSource

    @Binds
    @Singleton
    fun bindChatSocketService(chatSocketService: ChatSocketServiceImpl): ChatSocketService
}