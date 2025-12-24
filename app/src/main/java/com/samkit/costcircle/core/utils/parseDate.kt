package com.samkit.costcircle.core.utils

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// Helper to extract day and month.
// Input: "2025-12-24T15:30:00Z" (ISO format) -> Output: Pair("24", "DEC")
fun parseDateString(dateString: String?): Pair<String, String> {
    if (dateString.isNullOrBlank()) return Pair("--", "---")

    return try {
        // Attempt to parse ISO 8601
        val date = ZonedDateTime.parse(dateString)
        val day = date.dayOfMonth.toString()
        val month = date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase()
        Pair(day, month)
    } catch (e: Exception) {
        // Fallback if format doesn't match
        Pair("??", "???")
    }
}