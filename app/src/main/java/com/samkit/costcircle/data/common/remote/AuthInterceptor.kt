package com.samkit.costcircle.data.common.remote

import com.samkit.costcircle.data.auth.session.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sessionManager.getToken()

        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }

        val response = chain.proceed(request)

        if (response.code == 401) {
            sessionManager.clear()
        }
        response.close()

        return chain.proceed(request)
    }
}