package com.example.cafelogger.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

@Composable
fun HomeView(
    navController: NavController
) {
    // PLACEHOLDER
    val simpleItems = listOf(
        "Apple", "Banana", "Orange", "Grape", "Strawberry",
        "Blueberry", "Raspberry", "Pineapple", "Mango", "Watermelon",
        "Peach", "Plum", "Cherry", "Kiwi", "Lemon"
    )

    // Column that contains all elements
    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Profile picture")
            Text(text = "Ready for a drink?")
            Button(onClick = {navController.navigate(Views.MapsView.name)}) {
                Text(text = "Find a cafe")
            }
        }

        // Upload
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text(text = "Upload")
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = {navController.navigate(Views.UploadView.name)}) {
                    Text(text = "Lattes beans etc")
                }
            }
        }

        // Recent
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // The items block defines the content for each cell
            items(simpleItems) { item ->
                Card() {
                    Text(text = "Item $item")
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HomePreview() {
    val navController = rememberNavController()

    HomeView(navController)
}