package com.samkit.costcircle.data.group.repository

import com.samkit.costcircle.data.group.dto.GroupFinancialSummaryDto
import com.samkit.costcircle.data.group.dto.GroupSummaryDto
import com.samkit.costcircle.data.group.remote.GroupApiService

class GroupRepository(
    private val api: GroupApiService
) {

    /**
     * Groups (Home) screen
     * User-centric summary per group
     */
    suspend fun getGroupsSummary(): List<GroupSummaryDto> {
        return api.getGroupsSummary()
    }

    /**
     * Group details screen
     * Group-wide financial settlements
     */
    suspend fun getGroupFinancialSummary(
        groupId: Long
    ): GroupFinancialSummaryDto {
        return api.getGroupFinancialSummary(groupId)
    }
}
