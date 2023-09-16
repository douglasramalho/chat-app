package com.example.chatapp.mediastorage

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import okhttp3.HttpUrl.Companion.toHttpUrl
import javax.inject.Inject

class FirebaseCloudStorageHelper @Inject constructor() : MediaStorageHelper {

    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    private val storageRef = storage.reference

    override fun uploadImage(folder: String, uri: Uri) {
        val imagesRef = storageRef.child("$folder/${uri.lastPathSegment}")
        val uploadTask = imagesRef.putFile(uri)
        uploadTask.addOnFailureListener {
            Log.d("FirebaseCloudStorageHelper", "uploadImageFailure: $it")
        }.addOnSuccessListener {
            it.metadata?.bucket?.toHttpUrl()
            Log.d("FirebaseCloudStorageHelper", "uploadImageSuccess: $it")
        }
    }
}