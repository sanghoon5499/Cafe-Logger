package com.example.cafelogger.model

import kotlinx.serialization.Serializable

@Serializable
data class Entry (
    val title: String,
    val location: String,
    val type: String,
    val roastLevel: String,
    val origin: String,
    val process: String,
    val drinkStyle: String,
    val imageUri: String?,
    val timestamp: Long
)