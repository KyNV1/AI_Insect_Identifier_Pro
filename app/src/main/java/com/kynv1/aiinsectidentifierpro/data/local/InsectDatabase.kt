package com.kynv1.aiinsectidentifierpro.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kynv1.aiinsectidentifierpro.data.local.entity.InsectEntity

@Database(entities = [InsectEntity::class], version = 1, exportSchema = false)
abstract class InsectDatabase : RoomDatabase() {

    abstract fun insectDao(): InsectDao

    companion object {
        @Volatile
        private var INSTANCE: InsectDatabase? = null

        fun getDatabase(context: Context): InsectDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InsectDatabase::class.java,
                    "insect_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
