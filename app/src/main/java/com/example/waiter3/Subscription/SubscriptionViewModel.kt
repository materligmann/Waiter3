package com.example.waiter3.Subscription

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import centmoinshuitstudio.waiter3.ModelPreferencesManager
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import kotlinx.coroutines.launch
import com.android.billingclient.api.PurchasesUpdatedListener
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SubscriptionViewModel(context: Context): ViewModel() {

    private val context = context

    var onPurchaseComplete: (() -> Unit)? = null

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            Log.d("myTag", billingResult.toString())
            Log.d("myTag", "purchaseUpdatedListener")
            if (purchases != null) {
                for (purchase in purchases) {
                    viewModelScope.launch {
                        handlePurchase(purchase)
                        onPurchaseComplete?.let { it() }
                    }
                }
            }
        }

    suspend fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState === Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                ModelPreferencesManager.put(true, "KEY_PRO")
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                val ackPurchaseResult = withContext(Dispatchers.IO) {
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams.build())
                }
            } else {
                ModelPreferencesManager.put(true, "KEY_PRO")
            }
        }
    }

    private var billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    var productDetails: ProductDetails? = null

    fun purchase() {
        val productDetailsParamsList = listOf(
            productDetails?.let {
                Log.d("myTag", productDetails.toString())
                it.subscriptionOfferDetails?.get(0)?.let { it1 ->
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                        .setProductDetails(it)
                        .setOfferToken(it1.offerToken)
                        .build()
                }
            }
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val activity = context as Activity
        Log.d("myTag", billingFlowParams.toString())
        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    fun getPrice(completion: (String) -> Unit) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                    val queryProductDetailsParams =
                        QueryProductDetailsParams.newBuilder()
                            .setProductList(
                                ImmutableList.of(
                                    QueryProductDetailsParams.Product.newBuilder()
                                        .setProductId("35797467633")
                                        .setProductType(BillingClient.ProductType.SUBS)
                                        .build()))
                            .build()

                    billingClient.queryProductDetailsAsync(queryProductDetailsParams) {
                            billingResult,
                            productDetailsList ->
                        productDetails = productDetailsList.first()
                        val price = productDetailsList.first().subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.formattedPrice.toString()
                        if (price != null) {
                            completion(price.toString())
                        }
                    }
                }
            }
            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    fun buy(completion: () -> Unit) {
        this.onPurchaseComplete = completion
        purchase()
    }
}

private fun BillingClient.acknowledgePurchase(build: AcknowledgePurchaseParams) {

}
