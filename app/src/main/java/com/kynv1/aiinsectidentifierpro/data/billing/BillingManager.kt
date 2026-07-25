package com.kynv1.aiinsectidentifierpro.data.billing

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface BillingManager {
    val isPremium: StateFlow<Boolean>
    val productPrices: StateFlow<Map<String, String>>

    fun startConnection()
    fun launchBillingFlow(activity: Activity, productId: String)
    fun onDestroy()
}

class MockBillingManager(private val context: Context) : BillingManager {
    private val _isPremium = MutableStateFlow(false)
    override val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _productPrices = MutableStateFlow(
        mapOf(
            "premium_weekly" to "92.000 ₫",
            "premium_yearly" to "929.000 ₫",
            "premium_monthly" to "229.000 ₫"
        )
    )
    override val productPrices: StateFlow<Map<String, String>> = _productPrices.asStateFlow()

    override fun startConnection() {
        // Mock connection setup success
    }

    override fun launchBillingFlow(activity: Activity, productId: String) {
        Toast.makeText(context, "Mock Billing: Processing payment for $productId...", Toast.LENGTH_SHORT).show()
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            _isPremium.value = true
            Toast.makeText(context, "Mock Billing: Purchase Successful!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        // Cleanup mock resources
    }
}

class PlayBillingManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) : BillingManager, PurchasesUpdatedListener {

    private val _isPremium = MutableStateFlow(false)
    override val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _productPrices = MutableStateFlow<Map<String, String>>(emptyMap())
    override val productPrices: StateFlow<Map<String, String>> = _productPrices.asStateFlow()

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    override fun startConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryProductDetails()
                    queryPurchases()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Handle disconnection
            }
        })
    }

    private fun queryProductDetails() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("premium_weekly")
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("premium_yearly")
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("premium_monthly")
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val pricesMap = mutableMapOf<String, String>()
                for (productDetails in productDetailsList) {
                    val offerDetails = productDetails.subscriptionOfferDetails?.firstOrNull()
                    val price = offerDetails?.pricingPhases?.pricingPhaseList?.firstOrNull()?.formattedPrice
                        ?: "N/A"
                    pricesMap[productDetails.productId] = price
                }
                _productPrices.value = pricesMap
            }
        }
    }

    override fun launchBillingFlow(activity: Activity, productId: String) {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                val productDetails = productDetailsList.first()
                val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: ""

                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(
                        listOf(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .setOfferToken(offerToken)
                                .build()
                        )
                    )
                    .build()

                billingClient.launchBillingFlow(activity, billingFlowParams)
            } else {
                Toast.makeText(context, "Product not found on Play Store", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        coroutineScope.launch(Dispatchers.Main) {
                            _isPremium.value = true
                        }
                    }
                }
            } else {
                _isPremium.value = true
            }
        }
    }

    private fun queryPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                var premiumActive = false
                for (purchase in purchasesList) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        premiumActive = true
                        handlePurchase(purchase)
                    }
                }
                _isPremium.value = premiumActive
            }
        }
    }

    override fun onDestroy() {
        billingClient.endConnection()
    }
}
