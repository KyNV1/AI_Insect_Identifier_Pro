package com.kynv1.aiinsectidentifierpro.common

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import timber.log.Timber

object AdManager {
    private const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    private var mInterstitialAd: InterstitialAd? = null
    private var isLoading = false

    fun loadAd(context: Context, onAdLoaded: (() -> Unit)? = null) {
        if (mInterstitialAd != null || isLoading) {
            onAdLoaded?.invoke()
            return
        }

        isLoading = true
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            TEST_INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Timber.d("Ad failed to load: ${adError.message}")
                    mInterstitialAd = null
                    isLoading = false
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Timber.d("Ad was loaded.")
                    mInterstitialAd = interstitialAd
                    isLoading = false
                    onAdLoaded?.invoke()
                }
            }
        )
    }

    fun showAd(activity: Activity, onAdClosed: () -> Unit) {
        val ad = mInterstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Timber.d("Ad dismissed fullscreen content.")
                    mInterstitialAd = null
                    // Preload the next ad
                    loadAd(activity.applicationContext)
                    onAdClosed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                    Timber.e("Ad failed to show fullscreen content: ${adError.message}")
                    mInterstitialAd = null
                    onAdClosed()
                }

                override fun onAdShowedFullScreenContent() {
                    Timber.d("Ad showed fullscreen content.")
                }
            }
            ad.show(activity)
        } else {
            loadAd(activity.applicationContext)
            onAdClosed()
        }
    }
}
