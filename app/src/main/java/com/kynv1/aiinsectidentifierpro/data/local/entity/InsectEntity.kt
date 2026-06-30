package com.kynv1.aiinsectidentifierpro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kynv1.aiinsectidentifierpro.data.model.InsectInfo
import org.json.JSONArray

@Entity(tableName = "insects")
data class InsectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val imageUri: String,
    val commonName: String,
    val scientificName: String,
    val confidence: Int,
    val description: String,
    val characteristicsJson: String,
    val habitat: String,
    val dangerLevel: String,
    val dangerDescription: String,
    val timestamp: Long
) {
    fun toInsectInfo(): InsectInfo {
        val list = mutableListOf<String>()
        try {
            val jsonArray = JSONArray(characteristicsJson)
            for (i in 0 until jsonArray.length()) {
                list.add(jsonArray.getString(i))
            }
        } catch (e: Exception) {
            // Fallback nếu không phải JSON
            if (characteristicsJson.isNotBlank()) {
                list.addAll(characteristicsJson.split(";;"))
            }
        }
        return InsectInfo(
            commonName = commonName,
            scientificName = scientificName,
            confidence = confidence,
            description = description,
            characteristics = list,
            habitat = habitat,
            dangerLevel = dangerLevel,
            dangerDescription = dangerDescription
        )
    }

    companion object {
        fun fromInsectInfo(info: InsectInfo, imageUri: String): InsectEntity {
            val jsonArray = JSONArray()
            info.characteristics.forEach { jsonArray.put(it) }
            return InsectEntity(
                imageUri = imageUri,
                commonName = info.commonName,
                scientificName = info.scientificName,
                confidence = info.confidence,
                description = info.description,
                characteristicsJson = jsonArray.toString(),
                habitat = info.habitat,
                dangerLevel = info.dangerLevel,
                dangerDescription = info.dangerDescription,
                timestamp = System.currentTimeMillis()
            )
        }
    }
}
