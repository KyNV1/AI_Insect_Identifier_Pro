package com.kynv1.aiinsectidentifierpro.di

import android.content.Context
import com.kynv1.aiinsectidentifierpro.data.local.InsectDao
import com.kynv1.aiinsectidentifierpro.data.local.InsectDatabase
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
    fun provideGeminiServiceClient(): GeminiServiceClient {
        return GeminiServiceClient()
    }

    @Provides
    @Singleton
    fun provideInsectRepository(
        insectDao: InsectDao,
        geminiServiceClient: GeminiServiceClient
    ): InsectRepository {
        return InsectRepository(insectDao, geminiServiceClient)
    }
}
