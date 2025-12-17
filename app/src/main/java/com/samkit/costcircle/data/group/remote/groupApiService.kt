package com.samkit.costcircle.data.group.remote


import com.samkit.costcircle.data.group.dto.BalanceDto
import com.samkit.costcircle.data.group.dto.GroupDto
import com.samkit.costcircle.data.group.dto.SettlementDto
import retrofit2.http.GET
import retrofit2.http.Path

interface groupApiService {

    @GET("groups")
    suspend fun getMyGroups(): List<GroupDto>

    @GET("groups/{groupId}/balances")
    suspend fun getGroupBalances(
        @Path("groupId") groupId: Long
    ): List<BalanceDto>

    @GET("groups/{groupId}/settlements")
    suspend fun getGroupSettlements(
        @Path("groupId") groupId: Long
    ): List<SettlementDto>
}
