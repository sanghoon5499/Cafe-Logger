package com.example.cafelogger.Composable

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun Divider(size: Int, thickness: Int, color: Color) {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = size.dp),
        thickness = thickness.dp,
        color = color,
    )
}

@Composable
fun BackButton(navController: NavController) {
    Button(onClick = {navController.popBackStack()},
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A2B20)))
    {
        Text("<")
    }
}