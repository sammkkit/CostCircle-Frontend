package com.samkit.costcircle.data.group.dto


import kotlinx.serialization.Serializable

@Serializable
data class GroupStatsDto(
    val totalSpending: Double,
    val byCategory: List<CategoryStatDto>,
    val byMember: List<MemberStatDto>
)

@Serializable
data class CategoryStatDto(
    val category: String, // "FOOD", "TRAVEL"
    val total: Double
)

@Serializable
data class MemberStatDto(
    val name: String,
    val total: Double
)