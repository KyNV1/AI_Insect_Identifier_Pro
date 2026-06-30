package com.kynv1.aiinsectidentifierpro.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kynv1.aiinsectidentifierpro.data.local.entity.InsectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InsectDao {
    @Query("SELECT * FROM insects ORDER BY timestamp DESC")
    fun getAllInsectsFlow(): Flow<List<InsectEntity>>

    @Query("SELECT * FROM insects WHERE id = :id LIMIT 1")
    suspend fun getInsectById(id: Long): InsectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsect(insect: InsectEntity): Long

    @Delete
    suspend fun deleteInsect(insect: InsectEntity): Int

    @Query("DELETE FROM insects WHERE id = :id")
    suspend fun deleteInsectById(id: Long): Int
}
