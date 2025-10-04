package com.example.cafelogger.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.cafelogger.viewmodel.DetailsViewModel
import com.example.cafelogger.viewmodel.HomeViewModel
import com.example.cafelogger.viewmodel.UploadViewModel
import com.example.cafelogger.viewmodel.ViewModelFactory

enum class Views {
    HomeView,
    MapsView,
    DetailsView,
    UploadView
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val factory = ViewModelFactory(context)

    val homeViewModel: HomeViewModel = viewModel(factory = factory)
    val uploadViewModel: UploadViewModel = viewModel(factory = factory)
    val detailsViewModel: DetailsViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Views.HomeView.name
    ) {
        composable(Views.HomeView.name) {HomeView(navController, homeViewModel, detailsViewModel)}
        composable(Views.UploadView.name) {UploadView(navController, uploadViewModel)}
        composable(Views.MapsView.name) {MapsView(navController)}
        composable(Views.DetailsView.name) {DetailsView(navController, detailsViewModel)}
    }
}