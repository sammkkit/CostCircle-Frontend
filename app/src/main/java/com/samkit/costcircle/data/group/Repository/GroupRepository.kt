package com.samkit.costcircle.data.group.repository

import android.util.Log
import com.samkit.costcircle.data.group.dto.CreateGroupRequest
import com.samkit.costcircle.data.group.dto.CreateGroupResponse
import com.samkit.costcircle.data.group.dto.GroupFinancialSummaryDto
import com.samkit.costcircle.data.group.dto.GroupSummaryDto
import com.samkit.costcircle.data.group.remote.GroupApiService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

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

    /**
     * New group creation
     * Returns the response containing the new groupId
     */
    private val _groupsRefreshTrigger = MutableSharedFlow<Unit>(replay = 0)
    val groupsRefreshTrigger = _groupsRefreshTrigger.asSharedFlow()

    suspend fun createGroup(name: String): CreateGroupResponse {
        val response = api.createGroup(CreateGroupRequest(name))
        // Signal that the list needs to be re-fetched
        _groupsRefreshTrigger.emit(Unit)
        return response
    }
}
