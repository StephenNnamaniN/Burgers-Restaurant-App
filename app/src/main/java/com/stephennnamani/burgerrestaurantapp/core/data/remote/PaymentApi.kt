package com.stephennnamani.burgerrestaurantapp.core.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

data class CreatePayPalOrderRequest(
    val currencyCode: String,
    val amount: String,
    val referenceId: String? = null
)

data class CreatePayPalOrderResponse(
    val orderId: String
)

data class CapturePayPalOrderRequest(
    val orderId: String
)

data class CaptureOrderResponse(
    val status: String,
    val captureId: String? = null
)


interface PaymentApi {
    @POST("paypal/create-order")
    suspend fun createPayPalOrder(
        @Body requestBody: CreatePayPalOrderRequest
    ): CreatePayPalOrderResponse

    @POST("paypal/capture-order")
    suspend fun capturePayPalOrder(
        @Body requestBody: CapturePayPalOrderRequest
    ): CaptureOrderResponse
}