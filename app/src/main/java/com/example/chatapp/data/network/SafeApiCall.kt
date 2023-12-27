package com.example.chatapp.data.network

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal suspend inline fun <reified T> safeApiCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    crossinline apiCall: suspend () -> HttpResponse
): T {
    return withContext(dispatcher) {
        val response = apiCall.invoke()
        response.body()
    }
}