package com.kynv1.aiinsectidentifierpro.common

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import timber.log.Timber

object AnalyticsHelper {

    private const val EVENT_SCAN_PHOTO = "insect_scan_photo"
    private const val EVENT_SCAN_AUDIO = "insect_scan_audio"
    private const val EVENT_AI_CHAT = "ai_chat_sent"
    private const val EVENT_VIEW_DETAIL = "view_insect_detail"
    private const val EVENT_PAYWALL_VIEW = "paywall_viewed"
    private const val EVENT_SUBSCRIBE_SUCCESS = "subscribe_success"

    private const val PARAM_SPECIES_NAME = "species_name"
    private const val PARAM_CONFIDENCE = "confidence"
    private const val PARAM_SCAN_TYPE = "scan_type"
    private const val PARAM_PROMPT_LENGTH = "prompt_length"
    private const val PARAM_SUBSCRIPTION_PLAN = "subscription_plan"

    private var firebaseAnalytics: FirebaseAnalytics? = null

    fun init(context: Context) {
        if (firebaseAnalytics != null) return
        try {
            firebaseAnalytics = FirebaseAnalytics.getInstance(context.applicationContext)
            Timber.d("FirebaseAnalytics initialized.")
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize FirebaseAnalytics.")
        }
    }

    fun logPhotoScan(speciesName: String, confidence: Int) {
        logEvent(EVENT_SCAN_PHOTO, createScanBundle(speciesName, confidence, "photo"))
    }

    fun logAudioScan(speciesName: String, confidence: Int) {
        logEvent(EVENT_SCAN_AUDIO, createScanBundle(speciesName, confidence, "audio"))
    }

    fun logAiChat(promptText: String) {
        val bundle = Bundle().apply {
            putInt(PARAM_PROMPT_LENGTH, promptText.length)
        }
        logEvent(EVENT_AI_CHAT, bundle)
    }

    fun logViewInsectDetail(speciesName: String) {
        val bundle = Bundle().apply {
            putString(PARAM_SPECIES_NAME, speciesName)
        }
        logEvent(EVENT_VIEW_DETAIL, bundle)
    }

    fun logPaywallView() {
        logEvent(EVENT_PAYWALL_VIEW, null)
    }

    fun logSubscriptionSuccess(plan: String) {
        val bundle = Bundle().apply {
            putString(PARAM_SUBSCRIPTION_PLAN, plan)
        }
        logEvent(EVENT_SUBSCRIBE_SUCCESS, bundle)
    }

    private fun createScanBundle(speciesName: String, confidence: Int, scanType: String): Bundle {
        return Bundle().apply {
            putString(PARAM_SPECIES_NAME, speciesName)
            putInt(PARAM_CONFIDENCE, confidence)
            putString(PARAM_SCAN_TYPE, scanType)
        }
    }

    private fun logEvent(eventName: String, params: Bundle?) {
        try {
            firebaseAnalytics?.logEvent(eventName, params)
            Timber.d("Analytics Event Logged: $eventName -> $params")
        } catch (e: Exception) {
            Timber.e(e, "Error logging analytics event: $eventName")
        }
    }
}
