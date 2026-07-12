package com.kynv1.aiinsectidentifierpro.data.remote

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.ai.client.generativeai.type.RequestOptions
import com.kynv1.aiinsectidentifierpro.data.model.InsectInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiServiceClient {

    suspend fun identifyInsect(bitmap: Bitmap): InsectInfo? = withContext(Dispatchers.IO) {
        val apiKey = GeminiConfig.API_KEY
        if (apiKey.isBlank()) {
            return@withContext InsectInfo(
                commonName = "Thiếu Gemini API Key",
                scientificName = "API_KEY_MISSING",
                confidence = 0,
                description = "Vui lòng cấu hình API Key của Gemini trong file GeminiConfig.kt để sử dụng tính năng nhận diện thực tế.",
                characteristics = listOf("Chưa cấu hình API Key", "Hãy dán khóa API của bạn vào GeminiConfig.API_KEY"),
                habitat = "Chưa xác định",
                dangerLevel = "Low",
                dangerDescription = "Không nguy hiểm"
            )
        }

        try {
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
                  "confidence": 95, // Integer between 0 and 100 representing confidence percentage
                  "description": "A brief description of the species, its key features",
                  "characteristics": ["Key feature 1", "Key feature 2", "Key feature 3"],
                  "habitat": "Natural habitat of the species",
                  "dangerLevel": "Low" or "Medium" or "High",
                  "dangerDescription": "Description of the danger to humans or pets, warnings if venomous, bites or stings"
                }
                Do not write any markdown code blocks, comments, or backticks wrapper, just return the raw JSON object.
                If the image does not contain an insect, spider, bug, or any arthropod, return this exact JSON structure:
                {
                  "commonName": "Unrecognized",
                  "scientificName": "None",
                  "confidence": 0,
                  "description": "This image does not contain an identifiable insect or bug.",
                  "characteristics": [],
                  "habitat": "Unknown",
                  "dangerLevel": "Low",
                  "dangerDescription": "No information."
                }
            """.trimIndent()

            val model = GenerativeModel(
                modelName = "gemini-2.5-flash",
                apiKey = apiKey,
                generationConfig = config,
                systemInstruction = content { text(systemInstructionText) },
                requestOptions = RequestOptions(apiVersion = "v1beta")
            )

            val response = model.generateContent(
                content {
                    image(bitmap)
                    text("Identify this insect and return details in JSON format.")
                }
            )

            val responseText = response.text
            if (!responseText.isNullOrBlank()) {
                InsectInfo.fromJson(responseText)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            InsectInfo(
                commonName = "Lỗi nhận diện",
                scientificName = "API_ERROR",
                confidence = 0,
                description = "Có lỗi xảy ra khi kết nối tới Gemini API: ${e.localizedMessage}",
                characteristics = listOf("Lỗi mạng hoặc API", "Hãy kiểm tra lại internet hoặc API Key"),
                habitat = "Chưa xác định",
                dangerLevel = "Low",
                dangerDescription = "Không xác định"
            )
        }
    }

    suspend fun getChatResponse(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = GeminiConfig.API_KEY
        if (apiKey.isBlank()) {
            return@withContext when {
                prompt.contains("many different kinds", ignoreCase = true) -> 
                    "There are over 1 million described species of insects, representing more than half of all known living organisms. Entomologists estimate there could be 5 to 30 million species in total yet to be discovered!"
                prompt.contains("insects grow", ignoreCase = true) -> 
                    "Insects grow through a process called metamorphosis. Some undergo complete metamorphosis (egg -> larva -> pupa -> adult, like butterflies), while others undergo incomplete metamorphosis (egg -> nymph -> adult, like grasshoppers) shedding their exoskeletons as they grow."
                prompt.contains("flower to flower", ignoreCase = true) -> 
                    "Butterflies and other insects fly from flower to flower to feed on nectar, which is high in sugars. In doing so, they pollinate the flowers by transferring pollen grains from the male anther to the female stigma, helping plants reproduce."
                prompt.contains("largest insect", ignoreCase = true) -> 
                    "By weight, the heaviest insect is the Giant Weta from New Zealand (up to 70g). By length, the longest is the Giant Stick Insect (Phryganistria chinensis), measuring over 62 cm (24 inches)!"
                prompt.contains("insect and a spider", ignoreCase = true) -> 
                    "Insects have 3 body segments (head, thorax, abdomen), 6 legs, antennae, and often wings. Spiders have 2 body segments (cephalothorax, abdomen), 8 legs, no antennae, and no wings. Spiders belong to the class Arachnida, not Insecta."
                else -> 
                    "I am your AI Bug Specialist! To enable full interactive AI chat, please configure your Gemini API Key in the 'GeminiConfig.kt' file."
            }
        }

        try {
            val systemInstructionText = "You are an expert entomologist AI. Answer the user's questions about insects, spiders, bugs, or arthropods in a friendly, helpful, and highly detailed manner. Keep it concise but educational."
            val model = GenerativeModel(
                modelName = "gemini-2.5-flash",
                apiKey = apiKey,
                systemInstruction = content { text(systemInstructionText) },
                requestOptions = RequestOptions(apiVersion = "v1beta")
            )
            val response = model.generateContent(prompt)
            response.text ?: "No response from Gemini."
        } catch (e: Exception) {
            e.printStackTrace()
            "Connection error with Gemini API: ${e.localizedMessage}"
        }
    }
}
