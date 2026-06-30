package com.kynv1.aiinsectidentifierpro.data.local

import android.content.Context
import android.content.SharedPreferences

class OnboardingStore(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }

    companion object {
        private const val PREFS_NAME = "ai_insect_prefs"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }
}
