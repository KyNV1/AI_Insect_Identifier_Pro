package com.kynv1.aiinsectidentifierpro.data.remote

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.kynv1.aiinsectidentifierpro.data.model.InsectInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

class GeminiServiceClient {

    private val MODEL_NAMES = listOf(
        "gemini-2.0-flash",
        "gemini-2.5-flash",
        "gemini-1.5-flash",
        "gemini-flash-latest"
    )

    suspend fun identifyInsect(bitmap: Bitmap): InsectInfo? = withContext(Dispatchers.IO) {
        val apiKey = GeminiConfig.API_KEY
        if (apiKey.isBlank()) {
            return@withContext InsectInfo(
                commonName = "Thiếu API Key",
                scientificName = "API_KEY_MISSING",
                confidence = 0,
                description = "Chưa cấu hình API Key. Vui lòng dán khóa Gemini API Key vào file GeminiConfig.kt.",
                characteristics = listOf("Chưa cấu hình API Key"),
                habitat = "Chưa xác định",
                dangerLevel = "Low",
                dangerDescription = "Không có thông tin"
            )
        }

        val config = generationConfig {
            responseMimeType = "application/json"
            temperature = 0.1f
        }

        val systemInstructionText = """
            You are an expert entomologist AI. Identify the insect or bug in the provided image.
            You MUST respond ONLY with a JSON object matching this schema:
            {
              "commonName": "Common name (e.g. Honey Bee, Monarch Butterfly, Fire Ant)",
              "scientificName": "Scientific name",
              "confidence": 95,
              "description": "A brief description of the species, its key features",
              "characteristics": ["Key feature 1", "Key feature 2", "Key feature 3"],
              "habitat": "Natural habitat of the species",
              "dangerLevel": "Low" or "Medium" or "High",
              "dangerDescription": "Description of the danger to humans or pets, warnings if venomous, bites or stings"
            }
            Do not write any markdown code blocks, comments, or backticks wrapper, just return the raw JSON object.
        """.trimIndent()

        var lastErrorMessage = ""

        for (modelName in MODEL_NAMES) {
            try {
                val model = GenerativeModel(
                    modelName = modelName,
                    apiKey = apiKey,
                    generationConfig = config,
                    systemInstruction = content { text(systemInstructionText) }
                )

                val response = model.generateContent(
                    content {
                        image(bitmap)
                        text("Identify this insect and return details in JSON format.")
                    }
                )

                val responseText = response.text
                if (!responseText.isNullOrBlank()) {
                    val info = InsectInfo.fromJson(responseText)
                    if (info != null) return@withContext info
                }
            } catch (e: Exception) {
                lastErrorMessage = e.localizedMessage ?: e.message ?: "Unknown error"
                Timber.w("Identify insect model '$modelName' failed: $lastErrorMessage")
            }
        }

        InsectInfo(
            commonName = "Lỗi Gemini API",
            scientificName = "API_ERROR",
            confidence = 0,
            description = "Không thể gọi Gemini API thực tế: $lastErrorMessage. Vui lòng kiểm tra lại API Key hoặc mạng.",
            characteristics = listOf("Lỗi API thực tế", lastErrorMessage),
            habitat = "Chưa xác định",
            dangerLevel = "Low",
            dangerDescription = "Không có thông tin"
        )
    }

    suspend fun getChatResponse(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = GeminiConfig.API_KEY
        if (apiKey.isBlank()) {
            return@withContext "[Chưa dán API Key]\n\nVui lòng dán Gemini API Key vào file GeminiConfig.kt để sử dụng Chat AI thực tế."
        }

        val systemInstructionText = "You are an expert entomologist AI. Answer the user's questions about insects, spiders, bugs, or arthropods in a friendly, helpful, and highly detailed manner. Keep it concise but educational."

        var lastErrorMessage = ""

        for (modelName in MODEL_NAMES) {
            try {
                val model = GenerativeModel(
                    modelName = modelName,
                    apiKey = apiKey,
                    systemInstruction = content { text(systemInstructionText) }
                )
                val response = model.generateContent(prompt)
                val text = response.text
                if (!text.isNullOrBlank()) {
                    Timber.d("Gemini Chat SUCCESS with model: $modelName")
                    return@withContext text
                }
            } catch (e: Exception) {
                lastErrorMessage = e.localizedMessage ?: e.message ?: "Unknown error"
                Timber.w("Gemini Chat model '$modelName' failed: $lastErrorMessage")
            }
        }

        "[Lỗi kết nối Gemini API Thực Tế]\n\nChi tiết lỗi từ Google Cloud Server: $lastErrorMessage\n\nBạn hãy kiểm tra lại kết nối mạng hoặc API Key trong GeminiConfig.kt."
    }

    suspend fun getBiologicalInsightsByName(speciesName: String, confidence: Int): InsectInfo? = withContext(Dispatchers.IO) {
        val apiKey = GeminiConfig.API_KEY
        if (apiKey.isBlank()) {
            return@withContext null
        }

        val config = generationConfig {
            responseMimeType = "application/json"
            temperature = 0.2f
        }

        val systemInstructionText = """
            You are an expert entomologist AI. The user identified an insect species named "$speciesName".
            You MUST return a raw JSON object with 5 detailed answers covering:
            1. Key characteristics
            2. Seasonal occurrence
            3. Toxicity / danger to humans
            4. Day or night activity (diurnal/nocturnal)
            5. Geographic distribution
            
            Respond ONLY with JSON matching this schema:
            {
              "commonName": "$speciesName",
              "scientificName": "Scientific binomial name",
              "confidence": $confidence,
              "description": "Comprehensive biological summary of $speciesName",
              "characteristics": [
                "Đặc điểm: [Detail 1]",
                "Mùa xuất hiện: [Detail 2]",
                "Độc tính: [Detail 3]",
                "Tập tính hoạt động: [Detail 4 (Ban ngày / Ban đêm)]",
                "Phân bố: [Detail 5]"
              ],
              "habitat": "Habitat description",
              "dangerLevel": "Low" or "Medium" or "High",
              "dangerDescription": "Detailed safety advisory"
            }
            Do not include markdown code wrappers or backticks.
        """.trimIndent()

        for (modelName in MODEL_NAMES) {
            try {
                val model = GenerativeModel(
                    modelName = modelName,
                    apiKey = apiKey,
                    generationConfig = config,
                    systemInstruction = content { text(systemInstructionText) }
                )

                val response = model.generateContent("Provide biological insights for $speciesName")
                val responseText = response.text
                if (!responseText.isNullOrBlank()) {
                    val info = InsectInfo.fromJson(responseText)
                    if (info != null) return@withContext info
                }
            } catch (e: Exception) {
                Timber.w("Biological insights model '$modelName' failed: ${e.localizedMessage}")
            }
        }

        null
    }

    suspend fun identifyInsectFromAudioFile(audioFile: File?): InsectInfo? = withContext(Dispatchers.IO) {
        if (audioFile == null || !audioFile.exists() || audioFile.length() == 0L) {
            return@withContext null
        }

        val apiKey = GeminiConfig.API_KEY
        if (apiKey.isBlank()) {
            return@withContext null
        }

        val config = generationConfig {
            responseMimeType = "application/json"
            temperature = 0.1f
        }

        val systemInstructionText = """
            You are an expert entomologist AI specializing in bioacoustics and insect sound identification.
            Listen to the provided audio recording carefully (buzzing, chirping, humming, or stridulation sounds).
            Identify the exact insect species in the audio recording.
            You MUST respond ONLY with a JSON object matching this schema:
            {
              "commonName": "Common name (e.g. Honey Bee, Cicada, Cricket, Mosquito)",
              "scientificName": "Scientific binomial name (e.g. Apis mellifera)",
              "confidence": 92,
              "description": "Biological description of the species based on sound analysis",
              "characteristics": [
                "Đặc điểm: Key physical & acoustic feature",
                "Mùa xuất hiện: Active season",
                "Độc tính: Toxicity / Bite & Sting advisory",
                "Tập tính hoạt động: Activity timing (Ban ngày / Ban đêm)",
                "Phân bố: Geographic distribution"
              ],
              "habitat": "Habitat description",
              "dangerLevel": "Low",
              "dangerDescription": "Safety warning"
            }
            Do not include markdown wrappers or code blocks.
        """.trimIndent()

        val bytes = audioFile.readBytes()

        for (modelName in MODEL_NAMES) {
            try {
                val model = GenerativeModel(
                    modelName = modelName,
                    apiKey = apiKey,
                    generationConfig = config,
                    systemInstruction = content { text(systemInstructionText) }
                )

                val response = model.generateContent(
                    content {
                        blob("audio/m4a", bytes)
                        text("Listen to this audio recording and identify the insect species.")
                    }
                )

                val responseText = response.text
                if (!responseText.isNullOrBlank()) {
                    val info = InsectInfo.fromJson(responseText)
                    if (info != null) return@withContext info
                }
            } catch (e: Exception) {
                Timber.w("Audio identification model '$modelName' failed: ${e.localizedMessage}")
            }
        }

        null
    }
}
