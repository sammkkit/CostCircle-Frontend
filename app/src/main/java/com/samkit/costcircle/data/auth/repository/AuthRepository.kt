package com.samkit.costcircle.data.auth.repository

import com.samkit.costcircle.data.auth.dto.LoginRequestDto
import com.samkit.costcircle.data.auth.dto.RegisterRequestDto
import com.samkit.costcircle.data.auth.remote.AuthApiService
import com.samkit.costcircle.data.auth.session.SessionManager

class AuthRepository(
    private val api: AuthApiService,
    private val sessionManager: SessionManager
) {

    suspend fun login(email: String, password: String) {
        val response = api.login(
            LoginRequestDto(email, password)
        )
        sessionManager.saveToken(response.token)
        sessionManager.saveUserId(response.user.id)
    }

    fun getCurrentUserId(): Long = sessionManager.getUserId()
    suspend fun register(
        name: String,
        email: String,
        password: String
    ) {
        val response = api.register(
            RegisterRequestDto(name, email, password)
        )

    }

    fun logout() {
        sessionManager.clear()
    }

    fun isLoggedIn(): Boolean =
        sessionManager.getToken() != null
}
