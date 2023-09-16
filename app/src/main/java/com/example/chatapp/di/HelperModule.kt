package com.example.chatapp.di

import com.example.chatapp.mediastorage.FirebaseCloudStorageHelper
import com.example.chatapp.mediastorage.MediaStorageHelper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface HelperModule {

    @Binds
    @Singleton
    fun bindMediaStorageHelper(mediaStorage: FirebaseCloudStorageHelper): MediaStorageHelper
}