package com.kynv1.aiinsectidentifierpro.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kynv1.aiinsectidentifierpro.data.local.entity.InsectEntity
import com.kynv1.aiinsectidentifierpro.data.repository.InsectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: InsectRepository
) : ViewModel() {

    /**
     * `null` means "not loaded yet" — distinct from an emitted empty list, which means
     * "loaded, and genuinely empty". Starting at `emptyList()` made the empty state flash
     * before Room's first emission even for users with a full collection.
     */
    val historyList: StateFlow<List<InsectEntity>?> = repository.allInsectsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    /** Called only after the user confirms in the delete dialog — the deletion is permanent. */
    fun deleteInsect(id: Long) {
        viewModelScope.launch {
            repository.deleteInsectById(id)
        }
    }
}
