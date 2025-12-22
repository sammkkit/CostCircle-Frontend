package com.samkit.costcircle.data.common.remote

import com.samkit.costcircle.data.auth.session.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sessionManager.getToken()
        val originalRequest = chain.request()

        // 1. Build the request with the token if it exists
        val request = if (!token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token") // .header replaces existing, .addHeader duplicates
                .build()
        } else {
            originalRequest
        }

        // 2. Proceed with the request ONCE
        val response = chain.proceed(request)

        // 3. Check for 401 Unauthorized
        if (response.code == 401) {
            // Log out the user or clear session as the token is invalid
            sessionManager.clear()

            // Optional: You could trigger a navigation event to Login here
            // via a SharedFlow or EventBus if needed.
        }

        // 4. Return the response directly. DO NOT close it here.
        // The caller (Retrofit) is responsible for closing the response body.
        return response
    }
}