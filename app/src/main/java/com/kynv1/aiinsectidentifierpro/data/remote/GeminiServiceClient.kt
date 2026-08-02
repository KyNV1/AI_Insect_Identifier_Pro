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
                commonName = "Missing API Key",
                scientificName = "API_KEY_MISSING",
                confidence = 0,
                description = "API Key not configured. Please configure your Gemini API Key in GeminiConfig.kt.",
                characteristics = listOf("API Key Missing"),
                habitat = "Unknown",
                dangerLevel = "Low",
                dangerDescription = "No information"
            )
        }

        val config = generationConfig {
            responseMimeType = "application/json"
            temperature = 0.1f
        }

        val systemInstructionText = """
            You are an expert entomologist AI. Identify the insect or bug in the provided image.
            You MUST respond ONLY in English. Do NOT use Vietnamese or any other language.
            You MUST respond ONLY with a JSON object matching this schema:
            {
              "commonName": "English common name (e.g. House Mosquito, Honey Bee, Monarch Butterfly, Fire Ant)",
              "scientificName": "Scientific binomial name (e.g. Culex pipiens)",
              "confidence": 95,
              "description": "A brief description in English of the species and its key features",
              "characteristics": ["Key feature 1 in English", "Key feature 2 in English", "Key feature 3 in English"],
              "habitat": "Natural habitat of the species in English",
              "dangerLevel": "Low" or "Medium" or "High",
              "dangerDescription": "Description of the danger to humans or pets in English"
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
                        text("Identify this insect and return details in English JSON format.")
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
            commonName = "Gemini API Error",
            scientificName = "API_ERROR",
            confidence = 0,
            description = "Failed to query Gemini API: $lastErrorMessage. Please check your API Key or network connection.",
            characteristics = listOf("API Error", lastErrorMessage),
            habitat = "Unknown",
            dangerLevel = "Low",
            dangerDescription = "No information"
        )
    }

    suspend fun getChatResponse(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = GeminiConfig.API_KEY
        if (apiKey.isBlank()) {
            return@withContext "[API Key Missing]\n\nPlease paste your Gemini API Key in GeminiConfig.kt to start live AI chat."
        }

        val systemInstructionText = "You are an expert entomologist AI. Answer the user's questions about insects, spiders, bugs, or arthropods in English in a friendly, helpful, concise, and highly detailed manner. Always respond strictly in English."

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

        "[Gemini API Connection Error]\n\nDetails from Google Cloud Server: $lastErrorMessage\n\nPlease check your network connection or API Key in GeminiConfig.kt."
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
            You MUST respond ONLY in English. Do NOT use Vietnamese.
            Respond ONLY with JSON matching this schema:
            {
              "commonName": "$speciesName",
              "scientificName": "Scientific binomial name",
              "confidence": $confidence,
              "description": "Comprehensive biological summary in English of $speciesName",
              "characteristics": [
                "Characteristics: [Detail 1 in English]",
                "Seasonality: [Detail 2 in English]",
                "Toxicity: [Detail 3 in English]",
                "Activity: [Detail 4 (Diurnal / Nocturnal) in English]",
                "Distribution: [Detail 5 in English]"
              ],
              "habitat": "Habitat description in English",
              "dangerLevel": "Low" or "Medium" or "High",
              "dangerDescription": "Detailed safety advisory in English"
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

                val response = model.generateContent("Provide biological insights in English for $speciesName")
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
            Listen to the provided audio recording carefully. Identify the exact insect species in English.
            You MUST respond ONLY in English. Do NOT use Vietnamese or any non-English language.
            You MUST respond ONLY with a JSON object matching this schema:
            {
              "commonName": "English common name (e.g. House Mosquito, Honey Bee, Cicada, Cricket)",
              "scientificName": "Scientific binomial name (e.g. Culex pipiens, Apis mellifera)",
              "confidence": 92,
              "description": "Biological description of the species in English based on sound analysis",
              "characteristics": [
                "Characteristics: Key physical & acoustic feature in English",
                "Seasonality: Active season in English",
                "Toxicity: Toxicity / Bite & Sting advisory in English",
                "Activity: Activity timing (Diurnal / Nocturnal) in English",
                "Distribution: Geographic distribution in English"
              ],
              "habitat": "Habitat description in English",
              "dangerLevel": "Low" or "Medium" or "High",
              "dangerDescription": "Safety warning in English"
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
                        text("Listen to this audio recording and identify the insect species in English.")
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
