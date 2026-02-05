package com.samkit.costcircle.data.auth.session

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_PICTURE = "user_picture"
    }

    // ✅ Save everything in one go
    fun saveUserSession(token: String, userId: Long, name: String, email: String, picture: String?) {
        val editor = prefs.edit()
        editor.putString(KEY_JWT_TOKEN, token)
        editor.putLong(KEY_USER_ID, userId)
        editor.putString(KEY_USER_NAME, name)
        editor.putString(KEY_USER_EMAIL, email)
        if (picture != null) {
            editor.putString(KEY_USER_PICTURE, picture)
        }
        editor.apply()
    }

    // Keep individual save methods if needed for backward compatibility,
    // but saveUserSession is preferred for login.
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_JWT_TOKEN, token).apply()
    }

    fun saveUserId(userId: Long) {
        prefs.edit().putLong(KEY_USER_ID, userId).apply()
    }

    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("is_biometric_enabled", enabled).apply()
    }
    fun isBiometricEnabled(): Boolean {
        return prefs.getBoolean("is_biometric_enabled", false)
    }
    // --- Getters ---
    fun getUserId(): Long = prefs.getLong(KEY_USER_ID, 0)

    fun getToken(): String? = prefs.getString(KEY_JWT_TOKEN, null)

    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, "User")

    fun setUserName(name: String) {
        prefs.edit().putString(KEY_USER_NAME, name).apply()
    }
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, "")

    fun getUserPicture(): String? = prefs.getString(KEY_USER_PICTURE, null)

    fun isLoggedIn(): Boolean = getToken() != null

    fun clear() {
        prefs.edit().clear().apply()
    }
}