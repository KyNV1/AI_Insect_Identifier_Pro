package com.kynv1.aiinsectidentifierpro.data.local

import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Day key uses SimpleDateFormat, not java.time: minSdk 24 has no java.time without desugaring.
class UsageStore(
    private val prefs: SharedPreferences,
    private val today: () -> String = ::currentDayKey
) {

    fun hasFreeScanLeft(): Boolean = scansUsedToday() < FREE_SCANS_PER_DAY

    // Called only on success, so a network or Gemini failure never costs the user a free scan.
    // Returns true exactly once: on the success that should trigger the Play review prompt.
    fun recordSuccessAndCheckReview(): Boolean {
        val total = prefs.getInt(KEY_TOTAL_SUCCESS, 0) + 1
        val askForReview = total >= REVIEW_AFTER_SUCCESSFUL_SCANS &&
            !prefs.getBoolean(KEY_REVIEW_REQUESTED, false)
        val editor = prefs.edit()
            .putString(KEY_DAY, today())
            .putInt(KEY_USED_TODAY, scansUsedToday() + 1)
            .putInt(KEY_TOTAL_SUCCESS, total)
        if (askForReview) editor.putBoolean(KEY_REVIEW_REQUESTED, true)
        editor.apply()
        return askForReview
    }

    private fun scansUsedToday(): Int =
        if (prefs.getString(KEY_DAY, null) == today()) prefs.getInt(KEY_USED_TODAY, 0) else 0

    companion object {
        const val PREFS_NAME = "usage_prefs"
        const val FREE_SCANS_PER_DAY = 3
        const val REVIEW_AFTER_SUCCESSFUL_SCANS = 2

        private const val KEY_DAY = "scan_day"
        private const val KEY_USED_TODAY = "scans_used_today"
        private const val KEY_TOTAL_SUCCESS = "total_successful_scans"
        private const val KEY_REVIEW_REQUESTED = "review_requested"

        private fun currentDayKey(): String = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
    }
}
