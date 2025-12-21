package com.samkit.costcircle.data.group.dto

data class GroupSummaryDto(
    val groupId: Long,
    val groupName: String,
    val netAmount: Double,
    val direction: String, // YOU_OWE, YOU_ARE_OWED, SETTLED
    val memberCount: Int
)
