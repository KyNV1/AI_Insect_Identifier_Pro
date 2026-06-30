package com.kynv1.aiinsectidentifierpro.data.remote

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.kynv1.aiinsectidentifierpro.data.model.InsectInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiServiceClient {

    suspend fun identifyInsect(bitmap: Bitmap): InsectInfo? = withContext(Dispatchers.IO) {
        val apiKey = GeminiConfig.API_KEY
        if (apiKey.isBlank()) {
            // Trả về đối tượng mock thông báo lỗi thiếu API Key để người dùng cấu hình
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
                  "commonName": "Tên tiếng Việt thông dụng (ví dụ: Ong mật, Bướm Monarch, Kiến lửa)",
                  "scientificName": "Scientific name",
                  "confidence": 95, // Integer between 0 and 100 representing confidence percentage
                  "description": "Mô tả ngắn gọn về loài, đặc trưng cơ bản",
                  "characteristics": ["Đặc điểm nổi bật 1", "Đặc điểm nổi bật 2", "Đặc điểm nổi bật 3"],
                  "habitat": "Môi trường sống của loài",
                  "dangerLevel": "Low" or "Medium" or "High",
                  "dangerDescription": "Mô tả mức độ nguy hiểm đối với con người hoặc vật nuôi, cảnh báo nếu độc hoặc cắn"
                }
                Do not write any markdown code blocks, comments, or backticks wrapper, just return the raw JSON object.
                If the image does not contain an insect, spider, bug, or any arthropod, return this exact JSON structure:
                {
                  "commonName": "Không nhận diện được",
                  "scientificName": "None",
                  "confidence": 0,
                  "description": "Hình ảnh này không chứa côn trùng hoặc sâu bọ có thể nhận diện.",
                  "characteristics": [],
                  "habitat": "Không rõ",
                  "dangerLevel": "Low",
                  "dangerDescription": "Không có thông tin."
                }
            """.trimIndent()

            val model = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = apiKey,
                generationConfig = config,
                systemInstruction = content { text(systemInstructionText) }
            )

            // Gửi request kèm hình ảnh
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
            // Trả về lỗi dưới dạng InsectInfo để hiển thị lên UI thay vì crash
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
}
