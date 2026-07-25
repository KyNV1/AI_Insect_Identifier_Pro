package com.kynv1.aiinsectidentifierpro.di

import android.content.Context
import com.kynv1.aiinsectidentifierpro.data.billing.BillingManager
import com.kynv1.aiinsectidentifierpro.data.billing.MockBillingManager
import com.kynv1.aiinsectidentifierpro.data.billing.PlayBillingManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BillingModule {

    private const val USE_MOCK_BILLING = true

    @Provides
    @Singleton
    fun provideBillingManager(
        @ApplicationContext context: Context
    ): BillingManager {
        return if (USE_MOCK_BILLING) {
            MockBillingManager(context)
        } else {
            val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
            PlayBillingManager(context, scope).apply {
                startConnection()
            }
        }
    }
}
