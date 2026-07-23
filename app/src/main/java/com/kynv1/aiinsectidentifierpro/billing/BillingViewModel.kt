package com.kynv1.aiinsectidentifierpro.billing

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import com.kynv1.aiinsectidentifierpro.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val repository: BillingRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val isPremium: StateFlow<Boolean> = userPreferences.isPremium
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val productDetailsList: StateFlow<List<ProductDetails>> = repository.productDetailsList

    val purchaseEvent: StateFlow<PurchaseState> = repository.purchaseEvent

    private val _uiState = MutableStateFlow<BillingUiState>(BillingUiState.Idle)
    val uiState: StateFlow<BillingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            purchaseEvent.collect { state ->
                when (state) {
                    is PurchaseState.Idle -> _uiState.value = BillingUiState.Idle
                    is PurchaseState.Loading -> _uiState.value = BillingUiState.Loading
                    is PurchaseState.Success -> {
                        _uiState.value = BillingUiState.Success
                    }
                    is PurchaseState.Cancelled -> _uiState.value = BillingUiState.Idle
                    is PurchaseState.Error -> _uiState.value = BillingUiState.Error(state.message)
                }
            }
        }
    }

    fun launchPurchaseFlow(activity: Activity, productId: String) {
        repository.launchPurchaseFlow(activity, productId)
    }

    fun restorePurchases() {
        viewModelScope.launch {
            _uiState.value = BillingUiState.Loading
            repository.queryActivePurchases()
            _uiState.value = BillingUiState.Idle
        }
    }

    fun clearError() {
        _uiState.value = BillingUiState.Idle
    }
}

sealed interface BillingUiState {
    object Idle : BillingUiState
    object Loading : BillingUiState
    object Success : BillingUiState
    data class Error(val message: String) : BillingUiState
}
