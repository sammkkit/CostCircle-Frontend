package com.samkit.costcircle.data.group.dto

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class ExpenseCategory(
    val code: String,       // Matches Backend String
    val displayName: String,
    val icon: ImageVector,
    val color: Color        // Background color for the icon circle
) {
    FOOD("FOOD", "Food", Icons.Rounded.Fastfood, Color(0xFFFFE0B2)),         // Orange
    TRAVEL("TRAVEL", "Travel", Icons.Rounded.Flight, Color(0xFFBBDEFB)),     // Blue
    ENTERTAINMENT("ENTERTAINMENT", "Fun", Icons.Rounded.Movie, Color(0xFFE1BEE7)), // Purple
    GROCERIES("GROCERIES", "Groceries", Icons.Rounded.ShoppingCart, Color(0xFFC8E6C9)), // Green
    RENT("RENT", "Rent", Icons.Rounded.Home, Color(0xFFFFCDD2)),             // Red
    BILLS("BILLS", "Bills", Icons.Rounded.Receipt, Color(0xFFCFD8DC)),       // Grey
    GENERAL("GENERAL", "General", Icons.Rounded.AttachMoney, Color(0xFFF5F5F5)); // Default

    companion object {
        // Helper to find the enum from the backend string
        fun fromCode(code: String?): ExpenseCategory {
            return entries.find { it.code == code } ?: GENERAL
        }
    }
}