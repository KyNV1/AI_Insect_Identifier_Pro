package com.kynv1.aiinsectidentifierpro.common

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.kynv1.aiinsectidentifierpro.data.local.PremiumStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

sealed interface PurchaseResult {
    object Idle : PurchaseResult
    data class Success(val isRestore: Boolean) : PurchaseResult
    object Cancelled : PurchaseResult
    data class Error(val message: String) : PurchaseResult
}

/**
 * Owns the Play Billing connection for the whole app (Singleton — a single BillingClient
 * per process, matching Google's guidance). Grants Premium by writing to [PremiumStore]
 * directly, so any screen reading premium status stays correct regardless of which screen
 * happened to trigger the purchase or restore.
 */
@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext context: Context,
    private val premiumStore: PremiumStore
) : PurchasesUpdatedListener {

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .enableAutoServiceReconnection()
        .build()

    private val _productDetails = MutableStateFlow<Map<String, ProductDetails>>(emptyMap())
    val productDetails: StateFlow<Map<String, ProductDetails>> = _productDetails.asStateFlow()

    private val _purchaseResult = MutableStateFlow<PurchaseResult>(PurchaseResult.Idle)
    val purchaseResult: StateFlow<PurchaseResult> = _purchaseResult.asStateFlow()

    init {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    queryProductDetails()
                    restorePurchases()
                } else {
                    Timber.w("Billing setup failed: ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                // enableAutoServiceReconnection() handles retrying the connection itself.
            }
        })

        // A subscription can lapse while the app just sits open in the background — re-check
        // on every foreground resume (not only cold start) so that gets caught without
        // requiring the user to fully restart the app.
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                restorePurchases()
            }
        })
    }

    private fun queryProductDetails() {
        val products = PRODUCT_IDS.map {
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(it)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        }
        val params = QueryProductDetailsParams.newBuilder().setProductList(products).build()
        billingClient.queryProductDetailsAsync(params) { billingResult, result ->
            if (billingResult.responseCode == BillingResponseCode.OK) {
                _productDetails.value = result.productDetailsList.associateBy { it.productId }
            } else {
                Timber.w("queryProductDetails failed: ${billingResult.debugMessage}")
            }
        }
    }

    /** Launches Google's payment sheet for [productId]. No-ops with an Error if prices haven't loaded yet. */
    fun launchPurchase(activity: Activity, productId: String) {
        val details = _productDetails.value[productId]
        val offerToken = details?.subscriptionOfferDetails?.firstOrNull()?.offerToken
        if (details == null || offerToken == null) {
            _purchaseResult.value = PurchaseResult.Error("This plan isn't available right now. Please try again shortly.")
            return
        }

        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(details)
            .setOfferToken(offerToken)
            .build()
        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()
        billingClient.launchBillingFlow(activity, flowParams)
    }

    /**
     * Syncs Premium status with Google Play.
     * Grants Premium for an active subscription, otherwise revokes it.
     * [notifyUi] controls whether the restore result is shown to the user.
     */
    fun restorePurchases(notifyUi: Boolean = false) {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode != BillingResponseCode.OK) {
                Timber.w("queryPurchases failed: ${billingResult.debugMessage}")
                if (notifyUi) {
                    _purchaseResult.value = PurchaseResult.Error("Couldn't check your purchases. Please try again.")
                }
                return@queryPurchasesAsync
            }
            val active = purchases.filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
            if (active.isNotEmpty()) {
                premiumStore.setPremium(true)
                active.forEach { acknowledgeIfNeeded(it) }
                if (notifyUi) _purchaseResult.value = PurchaseResult.Success(isRestore = true)
            } else {
                premiumStore.setPremium(false)
                if (notifyUi) _purchaseResult.value = PurchaseResult.Error("No previous purchase found for this account.")
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        when (billingResult.responseCode) {
            BillingResponseCode.OK -> {
                val purchased = purchases.orEmpty().filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
                if (purchased.isEmpty()) {
                    _purchaseResult.value = PurchaseResult.Error("Purchase did not complete.")
                    return
                }
                premiumStore.setPremium(true)
                purchased.forEach { acknowledgeIfNeeded(it) }
                _purchaseResult.value = PurchaseResult.Success(isRestore = false)
            }

            BillingResponseCode.USER_CANCELED -> _purchaseResult.value = PurchaseResult.Cancelled
            else -> _purchaseResult.value = PurchaseResult.Error(
                billingResult.debugMessage.ifBlank { "Purchase failed (code ${billingResult.responseCode})." }
            )
        }
    }

    private fun acknowledgeIfNeeded(purchase: Purchase) {
        if (purchase.isAcknowledged) return
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params) { result ->
            if (result.responseCode != BillingResponseCode.OK) {
                Timber.w("acknowledgePurchase failed: ${result.debugMessage}")
            }
        }
    }

    /** Called once the UI has reacted to the current [purchaseResult] (shown a toast/navigated). */
    fun consumePurchaseResult() {
        _purchaseResult.value = PurchaseResult.Idle
    }

    companion object {
        const val PRODUCT_WEEKLY = "weekly_premium"
        const val PRODUCT_MONTHLY = "monthly_premium"
        const val PRODUCT_YEARLY = "yearly_premium"
        private val PRODUCT_IDS = listOf(PRODUCT_WEEKLY, PRODUCT_MONTHLY, PRODUCT_YEARLY)
    }
}
