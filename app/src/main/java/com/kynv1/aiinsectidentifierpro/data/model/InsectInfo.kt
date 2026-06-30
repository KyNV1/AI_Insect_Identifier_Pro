package com.kynv1.aiinsectidentifierpro.data.model

data class InsectInfo(
    val commonName: String,
    val scientificName: String,
    val confidence: Int,
    val description: String,
    val characteristics: List<String>,
    val habitat: String,
    val dangerLevel: String,
    val dangerDescription: String
) {
    companion object {
        fun fromJson(jsonStr: String): InsectInfo? {
            return try {
                val json = org.json.JSONObject(jsonStr)
                val characteristicsArray = json.optJSONArray("characteristics")
                val characteristicsList = mutableListOf<String>()
                if (characteristicsArray != null) {
                    for (i in 0 until characteristicsArray.length()) {
                        characteristicsList.add(characteristicsArray.getString(i))
                    }
                }
                InsectInfo(
                    commonName = json.optString("commonName", "Không rõ tên"),
                    scientificName = json.optString("scientificName", "Unknown"),
                    confidence = json.optInt("confidence", 0),
                    description = json.optString("description", "Không có mô tả."),
                    characteristics = characteristicsList,
                    habitat = json.optString("habitat", "Không rõ"),
                    dangerLevel = json.optString("dangerLevel", "Low"),
                    dangerDescription = json.optString("dangerDescription", "Không có thông tin nguy hiểm.")
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
