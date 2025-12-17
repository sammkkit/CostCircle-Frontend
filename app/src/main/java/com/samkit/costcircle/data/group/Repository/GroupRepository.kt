package com.samkit.costcircle.data.group.Repository

import com.samkit.costcircle.core.logic.SettlementProcessor
import com.samkit.costcircle.core.model.SettlementSummary
import com.samkit.costcircle.data.group.mappers.toDomain
import com.samkit.costcircle.data.group.remote.groupApiService

class GroupRepository(
    private val api: groupApiService
) {

    suspend fun getGroupSettlementSummary(
        groupId: Long
    ): List<SettlementSummary> {

        val balances = api.getGroupBalances(groupId).map { it.toDomain() }
        val settlements = api.getGroupSettlements(groupId).map { it.toDomain() }

        return SettlementProcessor.buildSummaries(
            balances = balances,
            settlements = settlements
        )
    }
}
