package com.example.chatapp.ui.component

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.chatapp.ChatAppFileProvider
import com.example.chatapp.R
import com.example.chatapp.ui.theme.ChatAppTheme

@Composable
fun ProfilePicture(
    context: Context = LocalContext.current,
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

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            hasImage = uri != null
            profilePictureUri = uri
            uri?.let {
                val file = ChatAppFileProvider.createFile(context, it)
                val fileUri = Uri.fromFile(file)
                onPhotoSelected(fileUri.path)
            }
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
        stringResource(id = R.string.feature_sign_up_change_profile_photo)
    } ?: stringResource(id = R.string.common_add_profile_photo)

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
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
            )
        }

        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )
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