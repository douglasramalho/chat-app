package com.example.chatapp.mediastorage

import android.net.Uri
import com.example.chatapp.data.util.ResultStatus
import kotlinx.coroutines.flow.Flow

interface MediaStorageHelper {

    fun uploadImage(pathString: String, uri: Uri?): Flow<ResultStatus<String?>>

    fun getDownloadUrl(path: String): Flow<Uri>

    fun removeImage(path: String): Flow<Unit>
}