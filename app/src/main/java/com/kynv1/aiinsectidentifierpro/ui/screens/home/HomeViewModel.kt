package com.kynv1.aiinsectidentifierpro.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kynv1.aiinsectidentifierpro.data.model.InsectShort
import com.kynv1.aiinsectidentifierpro.data.model.HomeArticle
import com.kynv1.aiinsectidentifierpro.data.repository.InsectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val mostCommonInsects: List<InsectShort> = emptyList(),
    val gardenInsects: List<InsectShort> = emptyList(),
    val funFactsArticles: List<HomeArticle> = emptyList(),
    val pestControlArticles: List<HomeArticle> = emptyList(),
    val bugBiteArticles: List<HomeArticle> = emptyList(),
    val remarkableCollArticles: List<HomeArticle> = emptyList(),
    val isPremium: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: InsectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadInsects()
    }

    private fun loadInsects() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val allMock = repository.getMockInsects()
            val mostCommon = allMock.filter { it.category == "Most Common" }
            val garden = allMock.filter { it.category == "Garden Insect" }

            val allArticles = repository.getMockArticles()
            val funFacts = allArticles.filter { it.category == "Fun Bug Facts" }
            val pestControl = allArticles.filter { it.category == "Pest Control" }
            val bugBite = allArticles.filter { it.category == "Bug Bite Help" }
            val remarkableColl = allArticles.filter { it.category == "Remarkable Collection" }

            _uiState.value = _uiState.value.copy(
                mostCommonInsects = mostCommon,
                gardenInsects = garden,
                funFactsArticles = funFacts,
                pestControlArticles = pestControl,
                bugBiteArticles = bugBite,
                remarkableCollArticles = remarkableColl,
                isLoading = false
            )
        }
    }

    fun purchasePremium() {
        _uiState.value = _uiState.value.copy(isPremium = true)
    }
}
