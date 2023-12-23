package com.example.chatapp.mediastorage

import android.net.Uri
import com.example.chatapp.data.util.ResultStatus
import com.example.chatapp.di.DefaultDispatcher
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseCloudStorageHelper @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : MediaStorageHelper {

    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    private val storageRef = storage.reference

    override fun uploadImage(pathString: String, uri: Uri?): Flow<ResultStatus<String?>> =
        callbackFlow {
            withContext(defaultDispatcher) {
                uri?.let {
                    val imagesRef = storageRef.child(pathString)
                    val uploadTask = imagesRef.putFile(uri)

                    uploadTask
                        .continueWithTask {
                            imagesRef.downloadUrl
                        }
                        .addOnFailureListener {
                            trySend(ResultStatus.Error(it))
                        }
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                trySend(ResultStatus.Success(task.result.toString()))
                            } else trySend(ResultStatus.Error(task.exception))
                        }
                } ?: trySend(ResultStatus.Success(null))
            }

            awaitClose()
        }

    override fun getDownloadUrl(path: String): Flow<Uri> = callbackFlow {
        withContext(defaultDispatcher) {
            storageRef.child(path).downloadUrl.addOnSuccessListener { uri ->
                trySend(uri)
            }
        }

        awaitClose()
    }

    override fun removeImage(path: String): Flow<Unit> = callbackFlow {
        withContext(defaultDispatcher) {
            storageRef.child(path).delete()
        }

        awaitClose()
    }
}