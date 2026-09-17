package com.kynv1.aiinsectidentifierpro.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kynv1.aiinsectidentifierpro.data.local.entity.ChatMessageEntity

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    suspend fun getAllMessages(): List<ChatMessageEntity>

    @Insert
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearAll()
}
