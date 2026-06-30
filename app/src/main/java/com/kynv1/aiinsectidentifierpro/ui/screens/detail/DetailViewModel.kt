package com.kynv1.aiinsectidentifierpro.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kynv1.aiinsectidentifierpro.data.local.entity.InsectEntity
import com.kynv1.aiinsectidentifierpro.data.repository.InsectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(val insect: InsectEntity) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: InsectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState

    fun loadInsect(id: Long) {
        _uiState.value = DetailUiState.Loading
        viewModelScope.launch {
            try {
                val insect = repository.getInsectById(id)
                if (insect != null) {
                    _uiState.value = DetailUiState.Success(insect)
                } else {
                    _uiState.value = DetailUiState.Error("Không tìm thấy thông tin côn trùng.")
                }
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error("Lỗi tải dữ liệu: ${e.localizedMessage}")
            }
        }
    }
}
