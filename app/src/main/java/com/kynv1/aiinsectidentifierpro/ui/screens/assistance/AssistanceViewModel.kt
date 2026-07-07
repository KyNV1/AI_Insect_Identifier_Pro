package com.kynv1.aiinsectidentifierpro.ui.screens.assistance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kynv1.aiinsectidentifierpro.data.repository.InsectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Message(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class AssistanceUiState(
    val messages: List<Message> = emptyList(),
    val isSending: Boolean = false
)

@HiltViewModel
class AssistanceViewModel @Inject constructor(
    private val repository: InsectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssistanceUiState())
    val uiState: StateFlow<AssistanceUiState> = _uiState.asStateFlow()

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMessage = Message(
            id = System.nanoTime().toString(),
            text = text,
            isUser = true
        )
        
        val currentMessages = _uiState.value.messages.toMutableList()
        currentMessages.add(userMessage)
        
        _uiState.value = _uiState.value.copy(
            messages = currentMessages,
            isSending = true
        )

        viewModelScope.launch {
            val responseText = repository.getChatResponse(text)
            val aiMessage = Message(
                id = System.nanoTime().toString(),
                text = responseText,
                isUser = false
            )
            val updatedMessages = _uiState.value.messages.toMutableList()
            updatedMessages.add(aiMessage)
            
            _uiState.value = _uiState.value.copy(
                messages = updatedMessages,
                isSending = false
            )
        }
    }
}
