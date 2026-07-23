package com.kynv1.aiinsectidentifierpro.di

import android.content.Context
import com.kynv1.aiinsectidentifierpro.data.preferences.UserPreferences
import com.kynv1.aiinsectidentifierpro.billing.BillingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BillingModule {

    @Provides
    @Singleton
    fun provideBillingRepository(
        @ApplicationContext context: Context,
        userPreferences: UserPreferences
    ): BillingRepository {
        return BillingRepository(context, userPreferences)
    }
}
