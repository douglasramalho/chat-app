package com.example.chatapp.data.di

import com.example.chatapp.data.ChatSocketService
import com.example.chatapp.data.ChatSocketServiceImpl
import com.example.chatapp.data.RemoteDataSource
import com.example.chatapp.data.remote.RemoteDataSourceImpl
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
    fun bindRemoteDataSource(remoteDataSource: RemoteDataSourceImpl): RemoteDataSource

    @Binds
    @Singleton
    fun bindChatSocketService(chatSocketService: ChatSocketServiceImpl): ChatSocketService
}