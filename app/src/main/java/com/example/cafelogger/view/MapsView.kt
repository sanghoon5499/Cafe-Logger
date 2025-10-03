package com.example.cafelogger.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MapsView(
    navController: NavController
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Maps")
        Button(onClick = { navController.navigate(Views.HomeView.name) }) {
            Text(text = "Home View")
        }
        Button(onClick = { navController.navigate(Views.DetailsView.name) }) {
            Text(text = "Details View")
        }
        Button(onClick = { navController.navigate(Views.UploadView.name) }) {
            Text(text = "Upload View")
        }
    }
}