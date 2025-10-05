package com.example.cafelogger.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.cafelogger.Composable.Divider
import com.example.cafelogger.model.Entry
import com.example.cafelogger.viewmodel.DetailsViewModel
import com.example.cafelogger.viewmodel.ViewModelFactory

@Composable
fun DetailsView(
    navController: NavController,
    detailsViewModel: DetailsViewModel
) {
    val scrollState = rememberScrollState()
    var showDialog by remember { mutableStateOf(false) }
    val entry by detailsViewModel.entry.collectAsState()
    val currentEntry = entry

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete confirmation") },
            text = { Text("Are you sure you want to delete this entry?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        currentEntry?.let { detailsViewModel.deleteEntry(it) }
                        navController.popBackStack()
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (currentEntry != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState),
        ) {
            DetailsHeader(currentEntry, { showDialog = true })

            DetailsImageSection(currentEntry)

            Divider(24, 2, Color.Gray)

            DetailsInfoSection(currentEntry)
        }
    } else {
        CircularProgressIndicator()
    }
}

@Composable
fun DetailsHeader(
    entry: Entry,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = entry.title,
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        OutlinedIconButton(
            onClick = onDeleteClick,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color.Gray)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete entry",
                tint = Color(0xFFFF2C2C)
            )
        }
    }
}

@Composable
fun DetailsImageSection(currentEntry: Entry) {
    AsyncImage(
        model = currentEntry.imageUri,
        contentDescription = "Coffee/bean Entry: ${currentEntry.title}",
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun DetailsInfoSection(currentEntry: Entry) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ReusableRow("Type", currentEntry.type)
        ReusableRow("Roast", currentEntry.roastLevel)
        ReusableRow("Origin", currentEntry.origin)
        ReusableRow("Process", currentEntry.process)
        ReusableRow("Location", currentEntry.location)
    }
}


@Composable
fun ReusableRow(text: String, parameter: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(0.3f)
        )
        OutlinedTextField(
            value = parameter,
            onValueChange = {},
            readOnly = true,
            textStyle = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(0.7f),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DetailsViewPreview() {
    val navController = rememberNavController()
    val factory = ViewModelFactory(LocalContext.current)
    val detailsViewModel: DetailsViewModel = viewModel(factory = factory)

    val entry = Entry(
        title = "Morning Brew",
        location = "Foundry Coffee Roasters, Waterloo",
        type = "Drink",
        roastLevel = "Light-Medium",
        origin = "Yirgacheffe, Ethiopia",
        process = "Washed",
        drinkStyle = "Pour Over",
        imageUri = "https://picsum.photos/seed/picsum/1200/1600",
        timestamp = System.currentTimeMillis()
    )

    detailsViewModel.selectEntry(entry)

    DetailsView(
        navController,
        detailsViewModel
    )
}