package com.example.chatapp.data.remote.di

import android.content.SharedPreferences
import com.example.chatapp.data.remote.ChatApiService
import com.example.chatapp.data.remote.MyHttpException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.plugins.resources.Resources
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
                logger = Logger.SIMPLE
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

    @Provides
    @Singleton
    @ApiHttpClient
    fun provideClient(
        sharedPreferences: SharedPreferences
    ): HttpClient {
        return HttpClient(Android) {
            expectSuccess = true
            HttpResponseValidator {
                handleResponseExceptionWithRequest { cause, request ->
                    val clientException = cause as? ClientRequestException ?: return@handleResponseExceptionWithRequest
                    val responseException = clientException.response
                    throw MyHttpException(responseException)
                }
            }

            install(Resources)

            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }

            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val accessToken = sharedPreferences.getString("accessToken", null)
                        accessToken?.let {
                            BearerTokens(
                                accessToken = it,
                                ""
                            )
                        }
                    }
                }
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