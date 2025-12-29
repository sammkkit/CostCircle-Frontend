package com.samkit.costcircle.data.group.remote

import com.samkit.costcircle.data.auth.dto.UserDto
import com.samkit.costcircle.data.group.dto.*
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GroupApiService {

    @GET("groups/summary")
    suspend fun getGroupsSummary(): List<GroupSummaryDto>

    @GET("groups/{groupId}/financial-summary")
    suspend fun getGroupFinancialSummary(
        @Path("groupId") groupId: Long
    ): GroupFinancialSummaryDto

    @POST("groups")
    suspend fun createGroup(
        @Body request: CreateGroupRequest
    ): CreateGroupResponse

    @POST("groups/{groupId}/expenses")
    suspend fun addExpense(
        @Path("groupId") groupId: Long,
        @Body request: AddExpenseRequest
    ): AddExpenseResponse

    // --- NEW: Bulk Add Members ---
    @POST("groups/{groupId}/members/bulk")
    suspend fun addMembersBulk(
        @Path("groupId") groupId: Long,
        @Body request: BulkMemberRequest
    ): BulkMemberResponse

    // --- NEW: Get Expense History ---
    @GET("groups/{groupId}/expenses")
    suspend fun getGroupExpenses(
        @Path("groupId") groupId: Long
    ): List<TransactionDto>

    // Add this to your existing interface
    @GET("groups/{groupId}/members")
    suspend fun getGroupMembers(
        @Path("groupId") groupId: Long
    ): List<UserDto>

    @POST("groups/{groupId}/settle")
    suspend fun settleUp(
        @Path("groupId") groupId: Long,
        @Body request: SettleUpRequest
    ): SettleUpResponse
    @GET("groups/{groupId}/stats")
    suspend fun getGroupStats(
        @Path("groupId") groupId: Long
    ): GroupStatsDto
    @POST("user/check-user")
    suspend fun checkUserExists(
        @Body request: Map<String, String> // We can just send a map { "email": "..." }
    ): Unit

    @DELETE("groups/{groupId}")
    suspend fun deleteGroup(
        @Path("groupId") groupId: Long
    ): Unit // We just need 200 OK

    // In GroupApiService.kt

    @POST("user/fcm-token")
    suspend fun updateFcmToken(@Body body: Map<String, String>)

    @GET("user/activity")
    suspend fun getUserActivity(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): List<TransactionDto>
}