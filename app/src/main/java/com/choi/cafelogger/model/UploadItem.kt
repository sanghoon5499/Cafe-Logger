package com.choi.cafelogger.model

data class UploadItem(
    val timestamp: Long,
    val location: String?,
    val type: String?,
    val roast: String?,
    val origin: String?,
    val process: String?,
    val drinkStyle: String?,
    val imageUri: String?
)