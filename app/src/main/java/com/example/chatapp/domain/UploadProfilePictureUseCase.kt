package com.example.chatapp.domain

import com.example.chatapp.data.dispatcher.AppDispatchers
import com.example.chatapp.data.dispatcher.Dispatcher
import com.example.chatapp.data.extension.errorMapping
import com.example.chatapp.data.repository.UserRepository
import com.example.chatapp.data.util.ResultStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UploadProfilePictureUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @Dispatcher(AppDispatchers.IO)
    private val dispatcher: CoroutineDispatcher,
) {

    operator fun invoke(filePath: String?): Flow<ResultStatus<String>> {
        return flow {
            emit(ResultStatus.Loading)
            filePath?.let {
                val image = userRepository.uploadProfilePicture(filePath)
                emit(ResultStatus.Success(image.id))
            } ?: emit(ResultStatus.Error())

        }.catch {
            emit(ResultStatus.Error(it.errorMapping()))
        }.flowOn(dispatcher)
    }
}