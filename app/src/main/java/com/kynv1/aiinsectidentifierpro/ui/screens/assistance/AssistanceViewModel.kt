package com.kynv1.aiinsectidentifierpro.ui.screens.assistance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kynv1.aiinsectidentifierpro.R
import com.kynv1.aiinsectidentifierpro.common.AnalyticsHelper
import com.kynv1.aiinsectidentifierpro.data.repository.InsectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class Message(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class AssistanceUiState(
    val messages: List<Message> = emptyList(),
    val isSending: Boolean = false,
    /** Set when the request failed outright; the screen resolves and shows it, then clears it. */
    val errorResId: Int? = null
)

@HiltViewModel
class AssistanceViewModel @Inject constructor(
    private val repository: InsectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssistanceUiState())
    val uiState: StateFlow<AssistanceUiState> = _uiState.asStateFlow()

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorResId = null)
    }

    fun sendMessage(text: String) {
        // Ignore while a reply is still in flight: two concurrent requests would race to
        // clear isSending, hiding the typing indicator while an answer is still pending.
        if (text.isBlank() || _uiState.value.isSending) return
        AnalyticsHelper.logAiChat(text)

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
            try {
                val responseText = repository.getChatResponse(text)
                val aiMessage = Message(
                    id = System.nanoTime().toString(),
                    text = responseText,
                    isUser = false
                )
                val updatedMessages = _uiState.value.messages.toMutableList()
                updatedMessages.add(aiMessage)

                _uiState.value = _uiState.value.copy(messages = updatedMessages)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, "Chat request failed")
                _uiState.value = _uiState.value.copy(
                    errorResId = R.string.assistance_error_generic
                )
            } finally {
                // Always clears, so a thrown exception can never leave the typing
                // indicator spinning with no way out.
                _uiState.value = _uiState.value.copy(isSending = false)
            }
        }
    }
}
