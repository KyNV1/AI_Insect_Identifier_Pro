package com.kynv1.aiinsectidentifierpro.data.local

import android.content.Context
import android.content.SharedPreferences

class PremiumStore(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun isPremium(): Boolean {
        return prefs.getBoolean(KEY_IS_PREMIUM, false)
    }

    fun setPremium(isPremium: Boolean) {
        prefs.edit().putBoolean(KEY_IS_PREMIUM, isPremium).apply()
    }

    companion object {
        private const val PREFS_NAME = "ai_insect_prefs"
        private const val KEY_IS_PREMIUM = "is_premium"
    }
}
