package com.kynv1.aiinsectidentifierpro.ui.screens.premium

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.android.billingclient.api.ProductDetails
import com.kynv1.aiinsectidentifierpro.common.BillingManager
import com.kynv1.aiinsectidentifierpro.common.PurchaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PaywallViewModel @Inject constructor(
    private val billingManager: BillingManager
) : ViewModel() {

    val productDetails: StateFlow<Map<String, ProductDetails>> = billingManager.productDetails
    val purchaseResult: StateFlow<PurchaseResult> = billingManager.purchaseResult

    fun launchPurchase(activity: Activity, plan: PlanType) {
        billingManager.launchPurchase(activity, plan.productId)
    }

    fun restorePurchases() {
        billingManager.restorePurchases(notifyUi = true)
    }

    fun consumePurchaseResult() {
        billingManager.consumePurchaseResult()
    }
}
