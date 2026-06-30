package com.kynv1.aiinsectidentifierpro

import android.app.Application
import com.kynv1.aiinsectidentifierpro.data.local.InsectDatabase
import com.kynv1.aiinsectidentifierpro.data.repository.InsectRepository

class AIInsectIdentifierApp : Application() {
    val database by lazy { InsectDatabase.getDatabase(this) }
    val repository by lazy { InsectRepository(database.insectDao()) }
}
