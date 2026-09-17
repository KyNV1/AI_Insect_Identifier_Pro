package com.kynv1.aiinsectidentifierpro.di

import android.content.Context
import com.kynv1.aiinsectidentifierpro.data.local.ChatMessageDao
import com.kynv1.aiinsectidentifierpro.data.local.InsectDao
import com.kynv1.aiinsectidentifierpro.data.local.InsectDatabase
import com.kynv1.aiinsectidentifierpro.data.local.OnboardingStore
import com.kynv1.aiinsectidentifierpro.data.local.PremiumStore
import com.kynv1.aiinsectidentifierpro.data.remote.GeminiServiceClient
import com.kynv1.aiinsectidentifierpro.data.repository.InsectRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): InsectDatabase {
        return InsectDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideInsectDao(database: InsectDatabase): InsectDao {
        return database.insectDao()
    }

    @Provides
    @Singleton
    fun provideChatMessageDao(database: InsectDatabase): ChatMessageDao {
        return database.chatMessageDao()
    }

    @Provides
    @Singleton
    fun provideGeminiServiceClient(): GeminiServiceClient {
        return GeminiServiceClient()
    }

    @Provides
    @Singleton
    fun provideInsectRepository(
        insectDao: InsectDao,
        chatMessageDao: ChatMessageDao,
        geminiServiceClient: GeminiServiceClient
    ): InsectRepository {
        return InsectRepository(insectDao, chatMessageDao, geminiServiceClient)
    }

    @Provides
    @Singleton
    fun provideOnboardingStore(@ApplicationContext context: Context): OnboardingStore {
        return OnboardingStore(context)
    }

    @Provides
    @Singleton
    fun providePremiumStore(@ApplicationContext context: Context): PremiumStore {
        return PremiumStore(context)
    }
}
