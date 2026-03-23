package com.stephennnamani.burgerrestaurantapp.core.data.remote


import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import com.google.api.Context
import com.paypal.android.corepayments.BuildConfig
import com.paypal.android.corepayments.CoreConfig
import com.paypal.android.corepayments.Environment
import com.paypal.android.paypalwebpayments.PayPalPresentAuthChallengeResult
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutClient
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutFinishStartResult
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutFundingSource
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutRequest
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class PayPalWebCheckoutCoordinator(
    appContext: Context,
    environment: Environment = Environment.SANDBOX
) {
    sealed class Event {
        data object  Started: Event()
        data class Approved(val orderId: String): Event()
        data class Failed(val message: String): Event()
        data object Canceled: Event()
        data object NoResult: Event()
    }
    private val tag = "BurgerPayPal"

    private val _events = MutableSharedFlow<Event>(
        replay = 0,
        extraBufferCapacity = 16,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val events: SharedFlow<Event> = _events

    private val config = CoreConfig(
        clientId = BuildConfig.PAYPAL_CLIENT_ID,
        environment = environment,
    )

    private val client = PayPalWebCheckoutClient(
        appContext,
        config,
        BuildConfig.PAYPAL_RETURN_URL
    )
    private var lastOrderId: String? = null

    fun startCheckout(
        activity: ComponentActivity,
        orderId: String,
        fundingSource: PayPalWebCheckoutFundingSource = PayPalWebCheckoutFundingSource.PAYPAL
    ){
        if (orderId.isBlank()){
            emit(Event.Failed("OrderId is blank. Server didn't create an order."))
            return
        }
        lastOrderId = orderId
        emit(Event.Started)

        val request = PayPalWebCheckoutRequest(
            orderId = orderId,
            fundingSource = fundingSource
        )

        when (val result = client.start(activity = activity, request)){
            is PayPalPresentAuthChallengeResult.Success -> {
                Log.d(tag, "startCheckout() -> Launched browser for orderId=$orderId")
                emit(Event.Approved(orderId))
            }
            is PayPalPresentAuthChallengeResult.Failure -> {
                val msg = result.error.errorDescription
                Log.e(tag, "startCheckout() -> Failure: $msg", result.error)
                emit(Event.Failed(msg))
                lastOrderId = null
            }
        }
    }

    fun handleReturnIntent(intent: Intent?) {
        if (intent == null) return

        when (val result = client.finishStart(intent)) {
            is PayPalWebCheckoutFinishStartResult.Success -> {
                val orderId = lastOrderId ?: "Unknown"
                Log.d(tag, "finishStart() -> orderId=$orderId")
                emit(Event.Approved(orderId))
                lastOrderId = null
            }
            is PayPalWebCheckoutFinishStartResult.Failure -> {
                val msg = result.error.errorDescription
                Log.e(tag, "finishStart() -> Failure: $msg", result.error)
                emit(Event.Failed(msg))
                lastOrderId = null
            }

            PayPalWebCheckoutFinishStartResult.NoResult -> {
                Log.d(tag, "finishStart() -> No result")
                emit(Event.NoResult)
            }

            else -> {}
        }
    }

    private fun emit(event: Event){
        _events.tryEmit(event)
    }
}