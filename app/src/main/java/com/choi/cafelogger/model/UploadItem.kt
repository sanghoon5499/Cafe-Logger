package com.choi.cafelogger.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class UploadItem(
    val timestamp: Long,
    val location: String?,
    val type: String?,
    val roast: String?,
    val origin: String?,
    val process: String?,
    val drinkStyle: String?,
    val imageUri: String?
) : Parcelable