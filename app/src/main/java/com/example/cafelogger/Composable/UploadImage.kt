package com.example.cafelogger.Composable

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File

private fun getTmpFileUri(context: Context): Uri {
    val imagesDir = File(context.filesDir, "images")
    if (!imagesDir.exists()) {
        imagesDir.mkdir()
    }
    val file = File.createTempFile(
        "image_", ".jpg", imagesDir
    )
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImageUploadArea(
    modifier: Modifier = Modifier,
    imageUri: Uri?,
    onImageCaptured: (Uri?) -> Unit
) {
    val context = LocalContext.current
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { isSuccess ->
            if (isSuccess) {
                onImageCaptured(tempImageUri)
            }
        }
    )
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            onImageCaptured(uri)
        }
    )
    val cameraPermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )


    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = "Upload an Image",
            color = Color(0xFF61646B),
            fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .border(
                    width = 1.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .clickable {showDialog = true},
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Captured Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Upload Image",
                        modifier = Modifier.size(48.dp),
                        tint = Color.Gray
                    )
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {Text("Choose an option") },
            text = { Text("Would you like to take a new photo or choose one from your gallery?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        if (cameraPermissionState.status.isGranted) {
                            val uriToLaunch = getTmpFileUri(context)
                            tempImageUri = uriToLaunch
                            cameraLauncher.launch(uriToLaunch)
                        } else {
                            cameraPermissionState.launchPermissionRequest()
                        }
                    }
                ) {
                    Text("Camera")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        galleryLauncher.launch("image/*") // Launch gallery
                    }
                ) {
                    Text("Gallery")
                }
            }
        )
    }
}