package com.stephennnamani.burgerrestaurantapp.core.data.repoImpl

import android.util.Log
import com.stephennnamani.burgerrestaurantapp.core.data.domain.PaymentRepository
import com.stephennnamani.burgerrestaurantapp.core.data.remote.CapturePayPalOrderRequest
import com.stephennnamani.burgerrestaurantapp.core.data.remote.CapturePayPalOrderResponse
import com.stephennnamani.burgerrestaurantapp.core.data.remote.CreatePayPalOrderRequest
import com.stephennnamani.burgerrestaurantapp.core.data.remote.PaymentApi

class PaymentRepositoryImpl(
    private val paymentApi: PaymentApi
): PaymentRepository {
    override suspend fun createPayPalOrder(
        currencyCode: String,
        amount: String,
        referenceId: String?
    ): Result<String>  = runCatching {
        Log.d("BurgerPayments", "createPayPalOrder -> $currencyCode $amount ref = $referenceId ")
        paymentApi.createPayPalOrder(
            CreatePayPalOrderRequest(
                currencyCode = currencyCode,
                amount = amount,
                referenceId = referenceId
            )
        ).orderId
    }.onFailure {
        Log.e("BurgerPayments", "createPayPalOrder failed-> ${it.message}", it)
    }

    override suspend fun capturePayPalOrder(orderId: String): Result<CapturePayPalOrderResponse> = runCatching {
        Log.d("BurgerPayments", "capturePayPalOrder -> $orderId")
        paymentApi.capturePayPalOrder(CapturePayPalOrderRequest(orderId))
    }.onFailure {
        Log.e("BurgerPayments", "capturePayPalOrder failed-> ${it.message}", it)
    }
}