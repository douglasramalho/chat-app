package com.example.chatapp.domain

import com.example.chatapp.data.dispatcher.AppDispatchers
import com.example.chatapp.data.dispatcher.Dispatcher
import com.example.chatapp.data.extension.errorMapping
import com.example.chatapp.data.repository.AuthRepository
import com.example.chatapp.data.util.ResultStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @Dispatcher(AppDispatchers.IO)
    private val dispatcher: CoroutineDispatcher,
) {

    operator fun invoke(): Flow<ResultStatus<Unit>> {
        return flow {
            emit(ResultStatus.Loading)
            authRepository.logout()
            emit(ResultStatus.Success(Unit))

        }.catch {
            emit(ResultStatus.Error(it.errorMapping()))
        }.flowOn(dispatcher)
    }
}