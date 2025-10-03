package com.example.cafelogger.view

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable

enum class Views {
    HomeView,
    MapsView,
    DetailsView,
    UploadView
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Views.HomeView.name
    ) {
        composable(Views.HomeView.name) {HomeView(navController)}
        composable(Views.MapsView.name) {MapsView(navController)}
        composable(Views.DetailsView.name) {DetailsView(navController)}
        composable(Views.UploadView.name) {UploadView(navController)}
    }
}