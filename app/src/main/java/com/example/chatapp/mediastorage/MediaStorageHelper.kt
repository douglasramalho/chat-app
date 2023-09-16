package com.example.chatapp.mediastorage

import android.net.Uri

interface MediaStorageHelper {

    fun uploadImage(folder: String, uri: Uri)
}