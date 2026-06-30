package com.kynv1.aiinsectidentifierpro.data.repository

import android.graphics.Bitmap
import com.kynv1.aiinsectidentifierpro.data.local.InsectDao
import com.kynv1.aiinsectidentifierpro.data.local.entity.InsectEntity
import com.kynv1.aiinsectidentifierpro.data.model.InsectInfo
import com.kynv1.aiinsectidentifierpro.data.remote.GeminiServiceClient
import kotlinx.coroutines.flow.Flow

class InsectRepository(
    private val insectDao: InsectDao,
    private val geminiServiceClient: GeminiServiceClient = GeminiServiceClient()
) {
    val allInsectsFlow: Flow<List<InsectEntity>> = insectDao.getAllInsectsFlow()

    suspend fun getInsectById(id: Long): InsectEntity? {
        return insectDao.getInsectById(id)
    }

    suspend fun insertInsect(insect: InsectEntity): Long {
        return insectDao.insertInsect(insect)
    }

    suspend fun deleteInsectById(id: Long) {
        insectDao.deleteInsectById(id)
    }

    suspend fun identifyInsect(bitmap: Bitmap): InsectInfo? {
        return geminiServiceClient.identifyInsect(bitmap)
    }
}
