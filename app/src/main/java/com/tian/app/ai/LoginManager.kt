package com.tian.app.ai

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object LoginManager {
    private const val PREF_NAME = "login_prefs"
    private const val KEY_TOKEN = "token"
    private const val KEY_USERNAME = "username"
    private const val KEY_PASSWORD = "password"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        prefs = EncryptedSharedPreferences.create(
            context,
            PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveLoginInfo(token: String, username: String, password: String) {
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            putString(KEY_USERNAME, username)
            putString(KEY_PASSWORD, password)
            apply()
        }
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)

    fun getPassword(): String? = prefs.getString(KEY_PASSWORD, null)

    fun isLoggedIn(): Boolean = !getToken().isNullOrEmpty()

    fun clearLoginInfo() {
        prefs.edit().clear().apply()
    }
} 