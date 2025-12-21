package com.samkit.costcircle.data.group.remote

import com.samkit.costcircle.data.group.dto.GroupSummaryDto
import com.samkit.costcircle.data.group.dto.GroupFinancialSummaryDto
import retrofit2.http.GET
import retrofit2.http.Path

interface GroupApiService {

    @GET("groups/summary")
    suspend fun getGroupsSummary(): List<GroupSummaryDto>

    @GET("groups/{groupId}/financial-summary")
    suspend fun getGroupFinancialSummary(
        @Path("groupId") groupId: Long
    ): GroupFinancialSummaryDto
}
