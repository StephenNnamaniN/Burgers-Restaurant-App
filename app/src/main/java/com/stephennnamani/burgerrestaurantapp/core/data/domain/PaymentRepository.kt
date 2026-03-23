package com.stephennnamani.burgerrestaurantapp.core.data.domain

import com.stephennnamani.burgerrestaurantapp.core.data.remote.CapturePayPalOrderResponse

interface PaymentRepository {
    suspend fun createPayPalOrder(
        currencyCode: String,
        amount: String,
        referenceId: String?
    ): Result<String>

    suspend fun capturePayPalOrder(
        orderId: String
    ): Result<CapturePayPalOrderResponse>
}