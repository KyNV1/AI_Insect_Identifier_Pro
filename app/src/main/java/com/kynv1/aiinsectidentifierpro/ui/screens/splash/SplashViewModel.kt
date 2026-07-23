package com.kynv1.aiinsectidentifierpro.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kynv1.aiinsectidentifierpro.data.local.OnboardingStore
import com.kynv1.aiinsectidentifierpro.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SplashNextDestination {
    object Onboarding : SplashNextDestination
    object Home : SplashNextDestination
    object Paywall : SplashNextDestination
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val onboardingStore: OnboardingStore,
    private val userPreferences: UserPreferences
) : ViewModel() {

    fun determineNextDestination(onResult: (SplashNextDestination) -> Unit) {
        viewModelScope.launch {
            if (!onboardingStore.isOnboardingCompleted()) {
                onResult(SplashNextDestination.Onboarding)
            } else {
                val isPremium = userPreferences.isPremium.first()
                if (isPremium) {
                    onResult(SplashNextDestination.Home)
                } else {
                    onResult(SplashNextDestination.Paywall)
                }
            }
        }
    }
}
