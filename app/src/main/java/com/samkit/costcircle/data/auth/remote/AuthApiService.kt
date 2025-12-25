package com.samkit.costcircle.data.auth.remote

import com.samkit.costcircle.data.auth.dto.*
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): LoginResponseDto

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequestDto
    ): RegisterResponseDto

    @POST("auth/google")
    suspend fun googleLogin(@Body request: GoogleLoginRequest): AuthResponse
}
