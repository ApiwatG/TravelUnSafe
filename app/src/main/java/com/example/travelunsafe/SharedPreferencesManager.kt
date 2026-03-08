package com.example.travelunsafe

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences("travel_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID      = "user_id"
        private const val KEY_USERNAME     = "username"
        private const val KEY_EMAIL        = "email"
        private const val KEY_ROLE         = "role"
        private const val KEY_SAVED_EMAIL  = "saved_email"   // friend's "remember email" feature
    }

    // ✅ Save after login / register
    fun saveLoginStatus(
        isLoggedIn: Boolean,
        userId: String,
        username: String,
        email: String,
        role: String
    ) {
        preferences.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
            putString(KEY_USER_ID,       userId)
            putString(KEY_USERNAME,      username)
            putString(KEY_EMAIL,         email)
            putString(KEY_ROLE,          role)
            apply()
        }
    }

    // ✅ Check if logged in
    fun isLoggedIn(): Boolean =
        preferences.getBoolean(KEY_IS_LOGGED_IN, false)

    // ✅ Get saved user_id  — use this for API calls like loadTrips(userId)
    fun getUserId(): String =
        preferences.getString(KEY_USER_ID, "") ?: ""

    fun getUsername(): String =
        preferences.getString(KEY_USERNAME, "") ?: ""

    fun getEmail(): String =
        preferences.getString(KEY_EMAIL, "") ?: ""

    fun clear() {
        preferences.edit().clear().apply()
    }
    fun getRole(): String =
        preferences.getString(KEY_ROLE, "") ?: ""

    // ✅ Logout — clears session, optionally keep userId for "remember me"
    fun logout(keepUserId: Boolean = false) {
        preferences.edit().apply {
            remove(KEY_IS_LOGGED_IN)
            remove(KEY_USERNAME)
            remove(KEY_EMAIL)
            remove(KEY_ROLE)
            if (!keepUserId) remove(KEY_USER_ID)
            apply()
        }
    }

    // ✅ Remember email feature (from friend's branch)
    fun saveEmail(email: String) {
        preferences.edit().putString(KEY_SAVED_EMAIL, email).apply()
    }

    fun getSavedEmail(): String =
        preferences.getString(KEY_SAVED_EMAIL, "") ?: ""

    fun clearSavedEmail() {
        preferences.edit().remove(KEY_SAVED_EMAIL).apply()
    }
}