package com.example.chatapp.data.remote

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal suspend inline fun <reified T> safeApiCall(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    crossinline apiCall: suspend () -> HttpResponse
): T {
    return withContext(dispatcher) {
        val response = apiCall.invoke()
        when (response.status) {
            HttpStatusCode.OK -> {
                response.body()
            }

            else -> throw MyHttpException(response)
        }
    }
}

class MyHttpException(private val response: HttpResponse) : RuntimeException(
    "HTTP ${response.status.value} ${response.status.description}"
) {

    val code: Int
        get() = response.status.value

    override val message: String
        get() = response.status.description
}