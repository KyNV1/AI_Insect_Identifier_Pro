package com.kynv1.aiinsectidentifierpro.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kynv1.aiinsectidentifierpro.data.local.entity.ChatMessageEntity
import com.kynv1.aiinsectidentifierpro.data.local.entity.InsectEntity

@Database(entities = [InsectEntity::class, ChatMessageEntity::class], version = 2, exportSchema = false)
abstract class InsectDatabase : RoomDatabase() {

    abstract fun insectDao(): InsectDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: InsectDatabase? = null

        // Purely additive (new table for chat history) — the existing `insects` table and
        // its data are untouched, so real users' saved scan history survives the update.
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `chat_messages` (
                        `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        `text` TEXT NOT NULL,
                        `isUser` INTEGER NOT NULL,
                        `timestamp` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): InsectDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InsectDatabase::class.java,
                    "insect_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
