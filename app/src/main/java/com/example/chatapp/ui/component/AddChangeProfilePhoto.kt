package com.example.chatapp.ui.component

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.chatapp.ChatAppFileProvider
import com.example.chatapp.R
import com.example.chatapp.ui.theme.ChatAppTheme

@Composable
fun ProfilePicture(
    onPhotoSelected: (path: String?) -> Unit
) {
    var hasImage by rememberSaveable {
        mutableStateOf(false)
    }

    var profilePictureUri by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }

    var showBottomSheet by rememberSaveable {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            hasImage = uri != null
            profilePictureUri = uri
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            hasImage = success

            if (success) {
                profilePictureUri?.let {
                    val file = ChatAppFileProvider.createFile(context, it)
                    val uri = Uri.fromFile(file)
                    onPhotoSelected(uri.path)
                }
            }
        }
    )

    val text = profilePictureUri?.let {
        "Change profile photo"
    } ?: "Add profile photo"

    Column(
        modifier = Modifier
            .clickable {
                showBottomSheet = true
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (hasImage && profilePictureUri != null) {
            AsyncImage(
                model = profilePictureUri,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.ic_upload_photo),
                contentDescription = null
            )
        }

        Text(text = text)
    }

    if (showBottomSheet) {
        ChangeProfilePhotoModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            takePhotoClick = {
                val uri = ChatAppFileProvider.getImageUri(context)
                profilePictureUri = uri
                hasImage = false
                cameraLauncher.launch(uri)
            },
            uploadPhotoClick = {
                imagePicker.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewProfilePicture() {
    ChatAppTheme {
        ProfilePicture {

        }
    }
}