package com.example.cafelogger.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.cafelogger.Composable.Divider
import com.example.cafelogger.R
import com.example.cafelogger.model.Entry
import com.example.cafelogger.viewmodel.DetailsViewModel
import com.example.cafelogger.viewmodel.HomeViewModel
import com.example.cafelogger.viewmodel.ViewModelFactory

@Composable
fun HomeView(
    navController: NavController,
    homeViewModel: HomeViewModel,
    detailsViewModel: DetailsViewModel
) {
    val recents by homeViewModel.recents.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // This effect runs when the composable enters the screen
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            // If the screen is resuming (coming into focus), reload the data
            if (event == Lifecycle.Event.ON_RESUME) {
                homeViewModel.loadRecents()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Header()

        Divider(16, 2, Color.Gray)

        UploadSection(navController)

        RecentsSection(recents, detailsViewModel, navController)
    }
}

@Composable
fun Header() {
    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.coffee_pin),
            contentDescription = "Home logo",
            Modifier.size(148.dp)
        )
        Text(text = "Cafe Logger",
            style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
fun UploadSection(navController: NavController) {
    Column(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
        Text(text = "Upload",
            style = MaterialTheme.typography.titleLarge)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {navController.navigate(Views.UploadView.name)},
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A2B20)),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text(text = "Lattes, beans, etc")
            }
        }
    }
}

@Composable
fun RecentsSection(recents: List<Entry>,
                   detailsViewModel: DetailsViewModel,
                   navController: NavController)
{
    Column(modifier = Modifier.padding(8.dp, vertical = 12.dp).fillMaxWidth()) {
        Text(text = "Recently Uploaded",
            style = MaterialTheme.typography.titleLarge)
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // The items block defines the content for each cell
        items(recents) { entry ->
            Card(
                onClick = {
                    detailsViewModel.selectEntry(entry)
                    navController.navigate(Views.DetailsView.name)
                }
            ) {
                Box {
                    AsyncImage(
                        model = entry.imageUri,
                        contentDescription = "Coffee/bean Entry: ${entry.title}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        contentScale = ContentScale.Crop
                    )
                }
                Text(
                    text = entry.title,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
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
    val detailsViewModel: DetailsViewModel = viewModel()

    HomeView(navController, homeViewModel, detailsViewModel)
}