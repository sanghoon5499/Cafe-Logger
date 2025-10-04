package com.example.cafelogger.view

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cafelogger.viewmodel.UploadViewModel
import com.example.cafelogger.viewmodel.ViewModelFactory
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

private val typeOptions = listOf("Beans", "Drink")
private val drinkStyleOptions = listOf("Latte", "Cappuccino","Espresso", "Pour Over")
private val RoastOptions = listOf("Light", "Light-Medium", "Medium", "Medium-Dark", "Dark")

@Composable
fun UploadView(
    navController: NavController,
    uploadViewModel: UploadViewModel
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var titleText           by remember { mutableStateOf("") }
    var locationText        by remember { mutableStateOf("") }
    var originText          by remember { mutableStateOf("") }
    var processText         by remember { mutableStateOf("") }
    var selectedType        by remember { mutableStateOf(typeOptions[0]) }
    var selectedDrinkStyle  by remember { mutableStateOf(drinkStyleOptions[0]) }
    var selectedRoast       by remember { mutableStateOf(RoastOptions[0]) }
    var imageBitmap         by remember { mutableStateOf<Bitmap?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Page title
        EntryHeader()

        // Horizonal line spacer
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 2.dp,
            color = Color.Black,
        )
        Spacer(Modifier.height(8.dp))

        // Title (name) of entry
        Title(
            value = titleText,
            onValueChange = { newText -> titleText = newText }
        )
        Spacer(Modifier.height(16.dp))

        // Location
        Location(
            value = locationText,
            onValueChange = { newText -> locationText = newText }
        )
        Spacer(Modifier.height(16.dp))

        // Type and Roast Level
        TypeAndRoast(
            selectedType = selectedType,
            onTypeSelected = { selectedType = it },
            selectedRoast = selectedRoast,
            onRoastSelected = { selectedRoast = it }
        )
        Spacer(Modifier.height(16.dp))

        // Origin
        Origin(
            value = originText,
            onValueChange = { newText -> originText = newText}
        )
        Spacer(Modifier.height(16.dp))

        // Process
        Process(
            value = processText,
            onValueChange = { newText -> processText = newText}
        )
        Spacer(Modifier.height(16.dp))

        // Drink Style
        DrinkStyleInput(
            selectedValue = selectedDrinkStyle,
            onStyleSelected = { selectedDrinkStyle = it }
        )
        Spacer(Modifier.height(16.dp))

        // Upload an Image Area
        ImageUploadArea(
            imageBitmap = imageBitmap,
            onImageCaptured = { bitmap -> imageBitmap = bitmap }
        )
        Spacer(Modifier.height(24.dp))

        // Final Upload Button
        Button(
            onClick = {
                val imageUriString = imageBitmap?.let {
                    saveBitmapAndGetUri(context, it)
                }

                // call the entry repository and throw the json in somewhere
                uploadViewModel.saveNewEntry(
                    titleText,
                    locationText,
                    selectedType,
                    selectedRoast,
                    originText,
                    processText,
                    selectedDrinkStyle,
                    imageUriString,
                    System.currentTimeMillis()
                )

                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A2B20)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Upload", color = Color.White)
        }
    }
}

@Composable
fun EntryHeader() {
    Text(
        text = "Found something noteworthy?",
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center
    )
}

@Composable
fun Title(
    value: String,
    onValueChange: (String) -> Unit
) {
    Text(
        text = "Title",
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF61646B),
        fontWeight = FontWeight.Bold
    )
    Spacer(Modifier.height(4.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Name this entry",
            color = Color(0xFFAFB1B6))},
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun Location(
    value: String,
    onValueChange: (String) -> Unit
) {
    Text(
        text = "Location",
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF61646B),
        fontWeight = FontWeight.Bold
    )
    Spacer(Modifier.height(4.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Enter an address or the cafe's name",
            color = Color(0xFFAFB1B6))},
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeAndRoast(
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    selectedRoast: String,
    onRoastSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Type Dropdown
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Type",
                color = Color(0xFF61646B),
                fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))

            var isTypeMenuExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = isTypeMenuExpanded,
                onExpandedChange = { isTypeMenuExpanded = !isTypeMenuExpanded }
            ) {
                OutlinedTextField(
                    value = selectedType,
                    textStyle = TextStyle(color = Color(0xFF000000)),
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTypeMenuExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = isTypeMenuExpanded,
                    onDismissRequest = { isTypeMenuExpanded = false }
                ) {
                    typeOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onTypeSelected(option)
                                isTypeMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.width(16.dp))

        // Roast Level Dropdown
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Roast Level",
                color = Color(0xFF61646B),
                fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))

            var isRoastMenuExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = isRoastMenuExpanded,
                onExpandedChange = { isRoastMenuExpanded = !isRoastMenuExpanded }
            ) {
                OutlinedTextField(
                    value = selectedRoast,
                    textStyle = TextStyle(color = Color(0xFF000000)),
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isRoastMenuExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = isRoastMenuExpanded,
                    onDismissRequest = { isRoastMenuExpanded = false }
                ) {
                    RoastOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onRoastSelected(option)
                                isRoastMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Origin(
    value: String,
    onValueChange: (String) -> Unit
) {
    Text(
        text = "Origin",
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF61646B),
        fontWeight = FontWeight.Bold
    )
    Spacer(Modifier.height(4.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Where were the beans harvested?",
            color = Color(0xFFAFB1B6)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun Process(
    value: String,
    onValueChange: (String) -> Unit
) {
    Text(
        text = "Process",
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF61646B),
        fontWeight = FontWeight.Bold
    )
    Spacer(Modifier.height(4.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Any fermentation process?",
            color = Color(0xFFAFB1B6)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinkStyleInput(
    selectedValue: String,
    onStyleSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = "Drink Style",
            color = Color(0xFF61646B),
            fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))

        var isDrinkStyleMenuExpanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = isDrinkStyleMenuExpanded,
            onExpandedChange = { isDrinkStyleMenuExpanded = !isDrinkStyleMenuExpanded }
        ) {
            OutlinedTextField(
                value = selectedValue,
                textStyle = TextStyle(color = Color(0xFF000000)),
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDrinkStyleMenuExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = isDrinkStyleMenuExpanded,
                onDismissRequest = { isDrinkStyleMenuExpanded = false }
            ) {
                drinkStyleOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onStyleSelected(option)
                            isDrinkStyleMenuExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImageUploadArea(
    modifier: Modifier = Modifier,
    imageBitmap: Bitmap?,
    onImageCaptured: (Bitmap?) -> Unit
) {
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap ->
            onImageCaptured(bitmap)
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
                .clickable {
                    if (cameraPermissionState.status.isGranted) {
                        cameraLauncher.launch()
                    } else {
                        cameraPermissionState.launchPermissionRequest()
                    } },
            contentAlignment = Alignment.Center
        ) {
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap.asImageBitmap(),
                    contentDescription = "Captured Image",
                    modifier = Modifier.fillMaxSize()
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
}

private fun saveBitmapAndGetUri(context: Context, bitmap: Bitmap): String? {
    val filename = "log_image_${System.currentTimeMillis()}.jpg"
    val file = File(context.filesDir, filename)
    return try {
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()
        Uri.fromFile(file).toString()
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}


@Preview(showBackground = true)
@Composable
fun UploadPreview() {
    val navController = rememberNavController()
    val factory = ViewModelFactory(LocalContext.current)
    val uploadViewModel: UploadViewModel = viewModel(factory = factory)

    UploadView(navController, uploadViewModel)
}

