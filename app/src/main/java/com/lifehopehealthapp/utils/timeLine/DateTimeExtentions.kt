package com.lifehopehealthapp.utils.timeLine

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
fun String.formatDateTime(originalFormat: String, ouputFormat: String): String {
    val date = LocalDateTime.parse(this, DateTimeFormatter.ofPattern(originalFormat, Locale.ENGLISH))
    return date.format(DateTimeFormatter.ofPattern(ouputFormat, Locale.ENGLISH))
}