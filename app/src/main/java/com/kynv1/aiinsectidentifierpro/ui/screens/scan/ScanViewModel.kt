package com.kynv1.aiinsectidentifierpro.ui.screens.scan

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kynv1.aiinsectidentifierpro.data.local.entity.InsectEntity
import com.kynv1.aiinsectidentifierpro.data.repository.InsectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject

sealed interface ScanUiState {
    object Idle : ScanUiState
    object Loading : ScanUiState
    data class Success(val insectId: Long) : ScanUiState
    data class Error(val message: String) : ScanUiState
}

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val repository: InsectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState: StateFlow<ScanUiState> = _uiState

    var selectedImageUri by mutableStateOf<Uri?>(null)
        private set

    fun onImageSelected(uri: Uri?) {
        selectedImageUri = uri
        _uiState.value = ScanUiState.Idle
    }

    fun resetState() {
        _uiState.value = ScanUiState.Idle
    }

    fun identifyInsect(context: Context) {
        val uri = selectedImageUri ?: run {
            _uiState.value = ScanUiState.Error("Vui lòng chụp hoặc chọn một bức ảnh trước.")
            return
        }

        _uiState.value = ScanUiState.Loading

        viewModelScope.launch {
            try {
                val bitmap = uriToBitmap(context, uri)
                if (bitmap == null) {
                    _uiState.value = ScanUiState.Error("Không thể đọc được file ảnh.")
                    return@launch
                }

                val insectInfo = repository.identifyInsect(bitmap)
                if (insectInfo != null) {
                    val entity = InsectEntity.fromInsectInfo(insectInfo, uri.toString())
                    val id = repository.insertInsect(entity)
                    _uiState.value = ScanUiState.Success(id)
                } else {
                    _uiState.value = ScanUiState.Error("Gemini không phản hồi hoặc trả về dữ liệu sai định dạng.")
                }
            } catch (e: Exception) {
                _uiState.value = ScanUiState.Error("Có lỗi xảy ra: ${e.localizedMessage}")
            }
        }
    }

    private fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            val maxDimension = 1024
            var scale = 1
            if (options.outWidth > maxDimension || options.outHeight > maxDimension) {
                val widthScale = options.outWidth / maxDimension
                val heightScale = options.outHeight / maxDimension
                scale = maxOf(widthScale, heightScale)
            }

            val scaleOptions = BitmapFactory.Options().apply {
                inSampleSize = scale
            }
            val scaledInputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(scaledInputStream, null, scaleOptions)
            scaledInputStream?.close()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
