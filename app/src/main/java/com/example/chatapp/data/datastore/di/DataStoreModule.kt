package com.example.chatapp.data.datastore.di

import com.example.chatapp.data.datastore.DataStoreStorage
import com.example.chatapp.data.datastore.DataStoreStorageImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataStoreModule {

    @Binds
    @Singleton
    fun bindDataStoreStorage(storage: DataStoreStorageImpl): DataStoreStorage
}