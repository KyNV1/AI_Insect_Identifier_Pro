package com.kynv1.aiinsectidentifierpro.common

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import timber.log.Timber
import java.io.File
import java.io.IOException

class AudioRecorderHelper(private val context: Context) {

    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    fun startRecording(): File? {
        stopRecording()

        val file = File(context.cacheDir, "sound_scan_${System.currentTimeMillis()}.m4a")
        outputFile = file

        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)

            try {
                prepare()
                start()
                Timber.d("Audio recording started: ${file.absolutePath}")
            } catch (e: IOException) {
                Timber.e(e, "Failed to prepare or start MediaRecorder")
                return null
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error starting MediaRecorder")
                return null
            }
        }
        return file
    }

    fun stopRecording(): File? {
        recorder?.let {
            try {
                it.stop()
                it.release()
                Timber.d("Audio recording stopped successfully.")
            } catch (e: Exception) {
                Timber.e(e, "Error stopping MediaRecorder")
            }
            recorder = null
        }
        return outputFile
    }

    fun cancelRecording() {
        stopRecording()
        outputFile?.delete()
        outputFile = null
    }
}
