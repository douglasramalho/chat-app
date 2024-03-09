package com.example.chatapp.data.network.di

import android.content.Context
import android.content.Intent
import com.example.chatapp.MainActivity
import com.example.chatapp.data.datastore.AppPreferencesDataSource
import com.example.chatapp.data.datastore.DataStorePreferencesDataSource
import com.example.chatapp.data.network.NetworkError
import com.example.chatapp.data.ws.ChatSocketService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.plugins.plugin
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
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
        @ApplicationContext context: Context,
        appPreferencesDataSource: AppPreferencesDataSource,
        dataStorePreferencesDataSource: DataStorePreferencesDataSource,
        chatSocketService: ChatSocketService,
    ): HttpClient {
        return HttpClient(Android) {
            expectSuccess = true
            HttpResponseValidator {
                handleResponseExceptionWithRequest { exception, _ ->
                    val clientException = exception as? ClientRequestException
                        ?: return@handleResponseExceptionWithRequest
                    val exceptionResponse = clientException.response
                    throw when (val status = exceptionResponse.status) {
                        HttpStatusCode.BadRequest -> NetworkError.BadRequest
                        HttpStatusCode.NotFound -> NetworkError.NotFound
                        HttpStatusCode.Conflict -> NetworkError.Conflict
                        HttpStatusCode.Unauthorized -> {
                            chatSocketService.closeSession()
                            appPreferencesDataSource.clear()
                            dataStorePreferencesDataSource.clear()

                            context.startActivity(
                                Intent(context, MainActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                            )

                            return@handleResponseExceptionWithRequest
                        }
                        else -> NetworkError.Generic(
                            status.value,
                            exceptionResponse.bodyAsText()
                        )
                    }
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

            /*install(Auth) {
                bearer {
                    loadTokens {
                        val accessToken = dataStorePreferencesDataSource.accessTokenFlow.firstOrNull()
                        Log.d("ApiModule", "provideClient: $accessToken")
                        accessToken?.let {
                            BearerTokens(
                                accessToken = it,
                                ""
                            )
                        }
                    }
                }
            }*/

            defaultRequest {
                url("https://chat-api.androidmoderno.com.br/")
                // url("http://192.168.1.68:8080/")
                contentType(ContentType.Application.Json)
            }

            engine {
                connectTimeout = 10_000
            }
        }.apply {
            plugin(HttpSend).intercept { request ->
                val accessToken = dataStorePreferencesDataSource.accessTokenFlow.firstOrNull()

                accessToken?.let {
                    request.headers {
                        append("Authorization", "Bearer $accessToken")
                    }
                }

                execute(request)
            }
        }
    }
}