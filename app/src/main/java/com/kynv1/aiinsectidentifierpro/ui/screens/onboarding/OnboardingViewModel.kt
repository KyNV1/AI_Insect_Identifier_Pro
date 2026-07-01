package com.kynv1.aiinsectidentifierpro.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import com.kynv1.aiinsectidentifierpro.data.local.OnboardingStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingStore: OnboardingStore
) : ViewModel() {

    fun completeOnboarding() {
        onboardingStore.setOnboardingCompleted(true)
    }
}
