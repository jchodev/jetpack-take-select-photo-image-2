package com.jerry.jetpack_take_select_photo_image_2.components

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage

import com.jerry.jetpack_take_select_photo_image_2.R
import java.io.File

@Composable
fun MyImageArea(
    uri: Uri? = null, //target url to preview
    directory: File? = null, // stored directory
    onSetUri : (Uri) -> Unit = {}, // selected / taken uri
    upload:(Uri) -> Unit = {},
) {
    val context = LocalContext.current
    val tempUri = remember { mutableStateOf<Uri?>(null) }
    val authority = stringResource(id = R.string.fileprovider)
    fun getTempUri(): Uri? {
        directory?.let {
            it.mkdirs()
            val file = File.createTempFile(
                "image_" + System.currentTimeMillis().toString(),
                ".jpg",
                it
            )

            return FileProvider.getUriForFile(
                context,
                authority,
                file
            )
        }
        return null
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            it?.let {
                onSetUri.invoke(it)
            }
        }
    )

    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {isSaved ->
            tempUri.value?.let {
                onSetUri.invoke(it)
            }
        }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted, proceed to step 2
            val tmpUri = getTempUri()
            tempUri.value = tmpUri
            tempUri.value?.let {
                takePhotoLauncher.launch(it)
            }

        } else {
            // Permission is denied, handle it accordingly
        }
    }

    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet){
        MyModalBottomSheet(
            onDismiss = {
                showBottomSheet = false
            },
            onTakePhotoClick = {
                showBottomSheet = false

                val permission = Manifest.permission.CAMERA
                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
                ) {
                    // Permission is already granted, proceed to step 2
                    val tmpUri = getTempUri()
                    tempUri.value = tmpUri
                    tempUri.value?.let {
                        takePhotoLauncher.launch(it)
                    }

                } else {
                    // Permission is not granted, request it
                    cameraPermissionLauncher.launch(permission)
                }
            },
            onPhotoGalleryClick = {
                showBottomSheet = false
                imagePicker.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            },
        )
    }

    Column (
        modifier = Modifier.fillMaxWidth()
    ) {

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    showBottomSheet = true
                }
            ) {
                Text(text = "Select / Take")
            }
        }

        //preview selfie
        uri?.let {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = it,
                    modifier = Modifier.size(
                        160.dp
                    ),
                    contentDescription = null,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))


            Button(
                onClick = {
                    upload.invoke(it)
                }
            ) {
                Text(text = "upload to server")
            }
        }

    }
}