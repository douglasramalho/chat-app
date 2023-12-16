package com.example.chatapp.data.remote.di

import android.util.Log
import com.example.chatapp.data.remote.ChatApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    @SocketHttpClient
    fun provideSocketClient(): HttpClient {
        return HttpClient(CIO) {
            install(WebSockets)
            install(Logging) {
                logger = LogcatLogger()
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    class LogcatLogger : Logger {
        override fun log(message: String) {
            Log.d("Logger", "log: $message")
        }

    }

    @Provides
    @Singleton
    @ApiHttpClient
    fun provideClient(): HttpClient {
        return HttpClient(Android) {
            install(Logging) {
                logger = LogcatLogger()
                level = LogLevel.ALL
            }

            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            defaultRequest {
                // Prod: https://chat-api.douglasmotta.com.br
                url("http://192.168.1.68:8080/")
                contentType(ContentType.Application.Json)
            }

            engine {
                connectTimeout = 30_000
            }
        }
    }

    @Provides
    @Singleton
    fun provideApiService(client: OkHttpClient): ChatApiService {
        // Prod: https://chat-api.douglasmotta.com.br
        return Retrofit.Builder()
            .baseUrl("http://192.168.1.68:8080/")
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder().add(
                        KotlinJsonAdapterFactory()
                    ).build()
                )
            )
            .client(client)
            .build()
            .create(ChatApiService::class.java)
    }
}