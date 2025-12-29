package com.samkit.costcircle.data.group.repository

import android.util.Log
import com.samkit.costcircle.data.auth.dto.UserDto
import com.samkit.costcircle.data.auth.session.SessionManager
import com.samkit.costcircle.data.group.dto.AddExpenseRequest
import com.samkit.costcircle.data.group.dto.AddExpenseResponse
import com.samkit.costcircle.data.group.dto.BulkMemberRequest
import com.samkit.costcircle.data.group.dto.BulkMemberResponse
import com.samkit.costcircle.data.group.dto.CreateGroupRequest
import com.samkit.costcircle.data.group.dto.CreateGroupResponse
import com.samkit.costcircle.data.group.dto.GroupFinancialSummaryDto
import com.samkit.costcircle.data.group.dto.GroupSummaryDto
import com.samkit.costcircle.data.group.dto.SettleUpRequest
import com.samkit.costcircle.data.group.dto.SettleUpResponse
import com.samkit.costcircle.data.group.dto.TransactionDto
import com.samkit.costcircle.data.group.remote.GroupApiService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class GroupRepository(
    private val api: GroupApiService,
    private val sessionManager: SessionManager
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

    /**
     * Add expense to group
     * Returns the response containing the new expenseId
     */
    suspend fun addExpense(groupId: Long, request: AddExpenseRequest): AddExpenseResponse {
        return api.addExpense(groupId, request)
    }

    // 1. Fetch Expense History
    suspend fun getGroupTransactions(groupId: Long): List<TransactionDto> {
        return api.getGroupExpenses(groupId) // Matches your new Node.js route
    }

    // 2. Bulk Add Members
    suspend fun addMembersBulk(groupId: Long, emails: List<String>): BulkMemberResponse {
        val response = api.addMembersBulk(groupId, BulkMemberRequest(emails)) //
        _groupsRefreshTrigger.emit(Unit) // Refresh UI after adding members
        return response
    }

    suspend fun getGroupMembers(groupId: Long): List<UserDto> {
        return api.getGroupMembers(groupId)
    }

    suspend fun settleUp(groupId: Long, request: SettleUpRequest): SettleUpResponse {
        val response = api.settleUp(groupId, request)

        // Signal refresh so Balances and Transactions update across the app
        _groupsRefreshTrigger.emit(Unit)

        return response
    }

    suspend fun checkUserExists(email: String) {
        try {
            // Backend expects { "email": "value" }
            val requestBody = mapOf("email" to email)
            api.checkUserExists(requestBody)
        } catch (e: Exception) {
            // Retrofit throws HttpException for non-2xx codes (like 404)
            // We re-throw a clean message for the UI
            throw Exception("User not found: $email")
        }
    }

    // Add this function
    suspend fun updateFcmToken(token: String) {
        // Backend expects: { "token": "..." }
        api.updateFcmToken(mapOf("token" to token))
    }
    suspend fun deleteGroup(groupId: Long) {
        api.deleteGroup(groupId)
        // Signal to refresh the list (Group is gone, so list must update)
        _groupsRefreshTrigger.emit(Unit)
    }

    suspend fun getUserActivity(page: Int = 1, limit: Int = 50): List<TransactionDto> {
        return api.getUserActivity(page, limit)
    }
}
