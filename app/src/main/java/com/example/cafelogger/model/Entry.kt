package com.example.cafelogger.model

data class Entry (
    val location: String,
    val type: String,
    val roastLevel: String,
    val origin: String,
    val process: String,
    val drinkStyle: String,
    val imageUri: String?
)