package com.example.chatapp.data.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.chatapp.CurrentUser
import com.example.chatapp.data.datastore.CurrentUserSerializer
import com.example.chatapp.data.di.ApplicationScope
import com.example.chatapp.data.dispatcher.AppDispatchers
import com.example.chatapp.data.dispatcher.Dispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    private val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(
        name = "com.example.chatapp.app_preferences"
    )

    @Provides
    @Singleton
    fun provideAppDataStorePreferences(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> {
        return context.appDataStore
    }

    @Provides
    @Singleton
    fun provideCurrentUserDataStore(
        @ApplicationContext context: Context,
        @Dispatcher(AppDispatchers.IO) dispatcher: CoroutineDispatcher,
        @ApplicationScope scope: CoroutineScope,
        currentUserSerializer: CurrentUserSerializer,
    ): DataStore<CurrentUser> {
        return DataStoreFactory.create(
            serializer = currentUserSerializer,
            scope = CoroutineScope(scope.coroutineContext + dispatcher),
        ) {
            context.dataStoreFile("current_user.pb")
        }
    }
}