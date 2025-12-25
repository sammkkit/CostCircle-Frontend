package com.samkit.costcircle.data.auth.repository

import com.samkit.costcircle.data.auth.dto.AuthResponse
import com.samkit.costcircle.data.auth.dto.GoogleLoginRequest
import com.samkit.costcircle.data.auth.remote.AuthApiService
import com.samkit.costcircle.data.auth.session.SessionManager

class AuthRepository(
    private val api: AuthApiService,
    private val sessionManager: SessionManager
) {

    // ✅ Google Login: Handles API call + Session Saving
    suspend fun googleLogin(idToken: String): Result<AuthResponse> {
        return try {
            // 1. Call API
            val response = api.googleLogin(GoogleLoginRequest(idToken))

            // 2. Save Session Data (Repository responsibility)
            sessionManager.saveToken(response.token)
            sessionManager.saveUserId(response.user.id)

            // 3. Return Success
            Result.success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    fun logout() {
        sessionManager.clear()
    }

    fun isLoggedIn(): Boolean = sessionManager.getToken() != null

    fun getCurrentUserId(): Long = sessionManager.getUserId()
}