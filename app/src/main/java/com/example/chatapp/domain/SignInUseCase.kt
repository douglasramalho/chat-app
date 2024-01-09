package com.example.chatapp.domain

import com.example.chatapp.data.dispatcher.AppDispatchers
import com.example.chatapp.data.dispatcher.Dispatcher
import com.example.chatapp.data.extension.errorMapping
import com.example.chatapp.data.repository.AuthRepository
import com.example.chatapp.data.util.ResultStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    @Dispatcher(AppDispatchers.IO)
    private val dispatcher: CoroutineDispatcher,
) {

    data class Params(
        val email: String,
        val password: String,
    )

    operator fun invoke(params: Params): Flow<ResultStatus<Unit>> {
        return flow {
            emit(ResultStatus.Loading)
            authRepository.signIn(
                username = params.email,
                password = params.password,
            )

            getUserInfoUseCase().collect()

            emit(ResultStatus.Success(Unit))

        }.catch {
            emit(ResultStatus.Error(it.errorMapping()))
        }.flowOn(dispatcher)
    }
}