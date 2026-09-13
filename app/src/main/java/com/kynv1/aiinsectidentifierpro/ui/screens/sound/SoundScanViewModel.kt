package com.kynv1.aiinsectidentifierpro.ui.screens.sound

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kynv1.aiinsectidentifierpro.R
import com.kynv1.aiinsectidentifierpro.common.AnalyticsHelper
import com.kynv1.aiinsectidentifierpro.common.AudioRecorderHelper
import com.kynv1.aiinsectidentifierpro.data.local.entity.InsectEntity
import com.kynv1.aiinsectidentifierpro.data.model.InsectInfo
import com.kynv1.aiinsectidentifierpro.data.repository.InsectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import java.io.File
import javax.inject.Inject

sealed interface SoundScanState {
    object Listening : SoundScanState
    object Analyzing : SoundScanState
    data class Success(val insectName: String, val confidence: Int, val id: Long) : SoundScanState
}

@HiltViewModel
class SoundScanViewModel @Inject constructor(
    private val repository: InsectRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val audioRecorder = AudioRecorderHelper(context)

    private val _scanState = MutableStateFlow<SoundScanState>(SoundScanState.Listening)
    val scanState: StateFlow<SoundScanState> = _scanState.asStateFlow()

    private val _secondsLeft = MutableStateFlow(5)
    val secondsLeft: StateFlow<Int> = _secondsLeft.asStateFlow()

    private var listenJob: Job? = null

    /** (Re)starts the 5s listen-then-analyze cycle, replacing any cycle already in flight. */
    fun startListening(hasPermission: Boolean) {
        listenJob?.cancel()
        _scanState.value = SoundScanState.Listening
        _secondsLeft.value = 5

        listenJob = viewModelScope.launch {
            val recordedFile = if (hasPermission) audioRecorder.startRecording() else null

            while (_secondsLeft.value > 0) {
                delay(1000)
                _secondsLeft.value -= 1
            }

            val audioFile = if (hasPermission) audioRecorder.stopRecording() else recordedFile
            _scanState.value = SoundScanState.Analyzing
            _scanState.value = analyze(audioFile)
        }
    }

    private suspend fun analyze(audioFile: File?): SoundScanState.Success {
        var audioInfo: InsectInfo? = null
        try {
            withTimeoutOrNull(15_000L) {
                audioInfo = repository.identifyInsectFromAudioFile(audioFile)
            }
        } catch (e: Exception) {
            Timber.e(e, "Gemini audio recognition error")
        }

        var targetId = 10009L
        val info = audioInfo
        val detectedName: String
        val detectedScientific: String
        val detectedConfidence: Int

        if (info != null &&
            !info.commonName.contains("Unrecognized", ignoreCase = true) &&
            !info.commonName.contains("Lỗi", ignoreCase = true) &&
            !info.commonName.contains("Thiếu", ignoreCase = true)
        ) {
            detectedName = info.commonName
            detectedScientific = info.scientificName
            detectedConfidence = info.confidence

            val entity = InsectEntity.fromInsectInfo(
                info,
                "android.resource://${context.packageName}/${R.drawable.img_sound_scan_acoustic_waves}"
            )
            targetId = repository.insertInsect(entity)
            AnalyticsHelper.logAudioScan(detectedName, detectedConfidence)
        } else {
            detectedName = context.getString(R.string.sound_scan_no_match)
            detectedScientific = ""
            detectedConfidence = 0
        }

        val displayName = if (detectedScientific.isNotBlank() &&
            !detectedScientific.equals("None", ignoreCase = true)
        ) {
            "$detectedName ($detectedScientific)"
        } else {
            detectedName
        }

        return SoundScanState.Success(
            insectName = displayName,
            confidence = detectedConfidence,
            id = targetId
        )
    }

    override fun onCleared() {
        super.onCleared()
        audioRecorder.cancelRecording()
    }
}
