package com.kynv1.aiinsectidentifierpro.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.kynv1.aiinsectidentifierpro.data.preferences.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BillingRepository(
    private val context: Context,
    private val userPreferences: UserPreferences
) : PurchasesUpdatedListener {

    private val TAG = "BillingRepository"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Product IDs for Subscriptions
    val WEEKLY_SUB = "weekly_premium"
    val MONTHLY_SUB = "monthly_premium"
    val YEARLY_SUB = "yearly_premium"

    private val productIds = listOf(WEEKLY_SUB, MONTHLY_SUB, YEARLY_SUB)

    private val _productDetailsList = MutableStateFlow<List<ProductDetails>>(emptyList())
    val productDetailsList: StateFlow<List<ProductDetails>> = _productDetailsList.asStateFlow()

    private val _isBillingResultReady = MutableStateFlow(false)
    val isBillingResultReady: StateFlow<Boolean> = _isBillingResultReady.asStateFlow()

    private val _purchaseEvent = MutableStateFlow<PurchaseState>(PurchaseState.Idle)
    val purchaseEvent: StateFlow<PurchaseState> = _purchaseEvent.asStateFlow()

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    init {
        startBillingConnection()
    }

    fun startBillingConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Billing Client setup successful")
                    queryProductDetails()
                    queryActivePurchases()
                } else {
                    Log.e(TAG, "Billing setup failed: ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.w(TAG, "Billing Client disconnected. Retrying...")
                // Retry connection in real scenarios, or handle gracefully
            }
        })
    }

    private fun queryProductDetails() {
        val productList = productIds.map { productId ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        }

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, detailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "Product details query successful: ${detailsList.size} items found")
                _productDetailsList.value = detailsList
                _isBillingResultReady.value = true
            } else {
                Log.e(TAG, "Failed to query product details: ${billingResult.debugMessage}")
            }
        }
    }

    fun queryActivePurchases() {
        if (!billingClient.isReady) return

        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                processPurchases(purchasesList)
            } else {
                Log.e(TAG, "Failed to query purchases: ${billingResult.debugMessage}")
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (purchases != null) {
                    processPurchases(purchases)
                    _purchaseEvent.value = PurchaseState.Success
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Log.i(TAG, "User canceled the purchase flow")
                _purchaseEvent.value = PurchaseState.Cancelled
            }
            else -> {
                Log.e(TAG, "Purchase failed: ${billingResult.debugMessage}")
                _purchaseEvent.value = PurchaseState.Error(billingResult.debugMessage)
            }
        }
    }

    private fun processPurchases(purchasesList: List<Purchase>) {
        var isAnyPremiumActive = false

        for (purchase in purchasesList) {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                isAnyPremiumActive = true
                if (!purchase.isAcknowledged) {
                    acknowledgePurchase(purchase)
                }
            }
        }

        scope.launch {
            userPreferences.setPremium(isAnyPremiumActive)
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient.acknowledgePurchase(params) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "Purchase acknowledged successfully")
                scope.launch {
                    userPreferences.setPremium(true)
                }
            } else {
                Log.e(TAG, "Failed to acknowledge purchase: ${billingResult.debugMessage}")
            }
        }
    }

    fun launchPurchaseFlow(activity: Activity, productId: String) {
        val productDetails = _productDetailsList.value.find { it.productId == productId }
        if (productDetails == null) {
            Log.e(TAG, "ProductDetails not found for $productId")
            _purchaseEvent.value = PurchaseState.Error("Product details not loaded yet. Please try again.")
            return
        }

        val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: ""

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        _purchaseEvent.value = PurchaseState.Loading
        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            Log.e(TAG, "Failed to launch billing flow: ${billingResult.debugMessage}")
            _purchaseEvent.value = PurchaseState.Error(billingResult.debugMessage)
        }
    }
}

sealed interface PurchaseState {
    object Idle : PurchaseState
    object Loading : PurchaseState
    object Success : PurchaseState
    object Cancelled : PurchaseState
    data class Error(val message: String) : PurchaseState
}
