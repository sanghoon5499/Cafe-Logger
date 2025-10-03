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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.rememberNavController
import com.example.cafelogger.viewmodel.HomeViewModel
import com.example.cafelogger.viewmodel.ViewModelFactory

@Composable
fun HomeView(
    navController: NavController,
    homeViewModel: HomeViewModel
) {
    val recents by homeViewModel.recents.collectAsState()

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
            items(recents) { entry ->
                Card() {
                    Text(text = entry.location)
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HomePreview() {
    val navController = rememberNavController()
    val factory = ViewModelFactory(LocalContext.current)
    val homeViewModel: HomeViewModel = viewModel(factory = factory)

    HomeView(navController, homeViewModel)
}