package com.kynv1.aiinsectidentifierpro.common

import android.content.Context
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import timber.log.Timber
import java.io.File

data class SoundClassificationResult(
    val speciesName: String,
    val scientificName: String,
    val confidence: Int
)

object SoundClassifierEngine {

    private const val MODEL_FILE = "yamnet.tflite"

    fun classifyAudio(context: Context, audioFile: File?): SoundClassificationResult {
        if (audioFile == null || !audioFile.exists() || audioFile.length() == 0L) {
            Timber.w("Audio file is invalid or empty.")
            return SoundClassificationResult("Không có file âm thanh", "Unrecognized", 0)
        }

        try {
            val hasAssetModel = context.assets.list("")?.contains(MODEL_FILE) == true
            if (hasAssetModel) {
                Timber.d("Loading TensorFlow Lite AudioClassifier from assets: $MODEL_FILE")
                val classifier = AudioClassifier.createFromFile(context, MODEL_FILE)
                Timber.d("TensorFlow Lite AudioClassifier initialized successfully: $classifier")
            }
        } catch (e: Exception) {
            Timber.e(e, "TensorFlow Lite AudioClassifier asset not found or failed.")
        }

        return SoundClassificationResult("Không thể nhận diện âm thanh", "Unrecognized", 0)
    }
}
