package com.example.travelunsafe

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("travel_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_EMAIL    = "saved_email"
        private const val KEY_REMEMBER = "remember_login"
    }

    fun saveEmail(email: String) {
        prefs.edit()
            .putString(KEY_EMAIL, email)
            .putBoolean(KEY_REMEMBER, true)
            .apply()
    }

    fun getSavedEmail(): String = prefs.getString(KEY_EMAIL, "") ?: ""

    fun isRemembered(): Boolean = prefs.getBoolean(KEY_REMEMBER, false)

    fun clearEmail() {
        prefs.edit()
            .remove(KEY_EMAIL)
            .putBoolean(KEY_REMEMBER, false)
            .apply()
    }
}