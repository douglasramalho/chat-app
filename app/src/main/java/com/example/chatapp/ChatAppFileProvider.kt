package com.example.chatapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.core.content.FileProvider
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import java.io.FileOutputStream


class ChatAppFileProvider : FileProvider(R.xml.file_paths) {

    companion object {
        fun getImageUri(context: Context): Uri {
            val tempFile = File.createTempFile("image", ".jpg", context.cacheDir).apply {
                createNewFile()
                deleteOnExit()
            }

            val authority = context.packageName + ".fileprovider"

            val uri = getUriForFile(
                context,
                authority,
                tempFile,
            )

            context.grantUriPermission(context.packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

            return uri
        }

        fun createFile(context: Context, uri: Uri): File {
            val directory = File(context.cacheDir, "temp")
            val file = File(
                directory,
                "image.jpg"
            )

            if (!directory.exists()) {
                directory.mkdirs()
            }

            file.createNewFile()
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val source = inputStream.source().buffer()
                val sink = file.sink().buffer()
                source.use { input ->
                    sink.use { output ->
                        output.writeAll(input)
                    }
                }
            }

            return file
        }
    }
}