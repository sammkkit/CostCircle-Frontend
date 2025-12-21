package com.samkit.costcircle.data.group.dto

data class GroupFinancialSummaryDto(
    val groupId: Long,
    val settlements: List<SettlementEntryDto>
)
