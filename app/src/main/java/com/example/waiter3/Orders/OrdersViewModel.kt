package com.example.waiter3.Orders

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import centmoinshuitstudio.waiter3.ModelPreferencesManager
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchaseHistoryParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryPurchaseHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrdersViewModel(context: Context): ViewModel() {

    private val purchasesResponseListener =
        PurchasesResponseListener { billingResult, purchases ->
            Log.d("myTag", billingResult.toString())
            if (purchases != null) {
                Log.d("myTag", "purchase  not null")
                Log.d("myTag", purchases.count().toString())
                if (purchases.isEmpty()) {
                    ModelPreferencesManager.put(false, "KEY_PRO")
                } else {
                    for (purchase in purchases) {
                        viewModelScope.launch {
                            Log.d("myTag", "purchaseResponseListener")
                            handlePurchase(purchase)
                        }
                    }
                }
            } else {
                Log.d("myTag", "purchase  null")
            }
        }

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            Log.d("myTag", billingResult.toString())
            Log.d("myTag", "purchaseUpdatedListener")
            if (purchases != null) {
                for (purchase in purchases) {
                    viewModelScope.launch {
                        handlePurchase(purchase)
                    }
                }
            }
        }

    suspend fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState === Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                Log.d("myTag", "akno")
                ModelPreferencesManager.put(true, "KEY_PRO")
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                val ackPurchaseResult = withContext(Dispatchers.IO) {
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams.build())
                }
            } else {
                ModelPreferencesManager.put(true, "KEY_PRO")
            }
        } else {
            Log.d("myTag", "not purchased")
        }
    }
    private var billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    fun connectToBilling(completion: () -> Unit) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                    completion.invoke()
                }
            }
            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })

    }

    fun checkPurchase() {
        viewModelScope.launch {
            Log.d("myTag","check purchase")
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)


            val purchasesResult = billingClient.queryPurchasesAsync(params.build(), purchasesResponseListener)

            billingClient.queryPurchasesAsync(params.build(), purchasesResponseListener)

            //val paramsHistory = QueryPurchaseHistoryParams.newBuilder()
              //  .setProductType(BillingClient.ProductType.SUBS)

            //val purchaseHistoryResult = billingClient.queryPurchaseHistory(paramsHistory.build())
            //purchaseHistoryResult.purchaseHistoryRecordList?.get(0)?.products?.get(0)
              //  ?.let {
                //    if (it == "35797467633") {
                  //      ModelPreferencesManager.put(true,"KEY_PRO")

                    //}
                //}
            //Log.d("myTag", purchaseHistoryResult.billingResult.toString())
        }
    }
}
