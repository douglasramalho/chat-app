package com.example.chatapp.data.util

import com.example.chatapp.data.extension.errorMapping
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

sealed interface ResultStatus<out T> {
    data object Loading : ResultStatus<Nothing>
    data class Success<T>(val data: T) : ResultStatus<T>
    data class Error(val exception: Throwable? = null) : ResultStatus<Nothing>
}

suspend fun <T> getFlowResult(get: suspend () -> T): Flow<ResultStatus<T>> {
    return flow {
        emit(ResultStatus.Loading)
        val result = get()
        emit(ResultStatus.Success(result))
    }.catch {
        emit(ResultStatus.Error(it.errorMapping()))
    }
}
