package com.flixxo.apps.flixxoapp.repositories.local

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val appId = "flixxo_pref"
    private val mSharedPreferences: SharedPreferences

    init {
        mSharedPreferences = context.getSharedPreferences(appId, Context.MODE_PRIVATE)
    }

    fun clear() {
        mSharedPreferences.edit().clear().apply()
    }

    fun putString(key: String, value: String) {
        mSharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String): String? {
        return mSharedPreferences.getString(key, null)
    }

    fun putLong(key: String, value: Long) {
        mSharedPreferences.edit().putLong(key, value).apply()
    }

    fun putDouble(key: String, value: Double) {
        mSharedPreferences.edit().putLong(key, value.toLong()).apply()
    }

    fun putInt(key: String, value: Int) {
        mSharedPreferences.edit().putInt(key, value).apply()
    }

    fun putBoolean(key: String, value: Boolean) {
        mSharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String): Boolean {
        return mSharedPreferences.getBoolean(key, false)
    }

    fun getInt(key: String): Int {
        return mSharedPreferences.getInt(key, -1)
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return mSharedPreferences.getInt(key, defaultValue)
    }

    fun getLong(key: String): Long {
        return mSharedPreferences.getLong(key, -1)
    }

    fun getDouble(key: String): Double {
        return mSharedPreferences.getLong(key, -1).toDouble()
    }

    fun clearKey(key: String) {
        mSharedPreferences.edit().remove(key).apply()
    }

    companion object {

        private var INSTANCE: PreferencesManager? = null

        fun getInstance(context: Context): PreferencesManager {
            if (INSTANCE == null) {
                INSTANCE = PreferencesManager(context)
            }

            return INSTANCE as PreferencesManager
        }
    }
}