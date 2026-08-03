package com.kynv1.aiinsectidentifierpro

import android.app.Application
import android.content.pm.ApplicationInfo
import com.kynv1.aiinsectidentifierpro.common.AnalyticsHelper
import com.kynv1.aiinsectidentifierpro.common.CrashlyticsTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class AIInsectIdentifierApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val isDebug = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        if (isDebug) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsTree())
        }
        AnalyticsHelper.init(this)
    }
}
