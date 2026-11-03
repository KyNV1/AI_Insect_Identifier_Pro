package com.kynv1.aiinsectidentifierpro.common

import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import timber.log.Timber

object InAppReviewHelper {

    // Activity scope, not the screen's: the scan screen navigates to Detail immediately, which would
    // cancel a screen-scoped flow. Play may also skip the dialog silently (quota, sideloaded build).
    fun request(activity: ComponentActivity) {
        activity.lifecycleScope.launch {
            try {
                val manager = ReviewManagerFactory.create(activity)
                val reviewInfo = manager.requestReview()
                manager.launchReview(activity, reviewInfo)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.w(e, "In-app review unavailable")
            }
        }
    }
}
