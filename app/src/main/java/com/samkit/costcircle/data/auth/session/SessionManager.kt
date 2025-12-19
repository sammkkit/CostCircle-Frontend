package com.samkit.costcircle.data.auth.session

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("session", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("jwt_token", token).apply()
    }
    fun saveUserId(userId: Long) {
        prefs.edit().putLong("user_id", userId).apply()
    }

    fun getUserId(): Long =
        prefs.getLong("user_id", 0)

    fun getToken(): String? =
        prefs.getString("jwt_token", null)

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
    fun clear() {
        prefs.edit().clear().apply()
    }
}
