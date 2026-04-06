package com.stephennnamani.burgerrestaurantapp.feature.payment.paypal

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephennnamani.burgerrestaurantapp.core.data.domain.CartRepository
import com.stephennnamani.burgerrestaurantapp.core.data.domain.PaymentRepository
import com.stephennnamani.burgerrestaurantapp.core.data.remote.PayPalWebCheckoutCoordinator
import com.stephennnamani.burgerrestaurantapp.feature.util.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PayPalPaymentResult(
    val orderId: String,
    val status: String,
    val captureId: String?
)

data class PayPalUiState(
    val state: RequestState<PayPalPaymentResult> = RequestState.Idle,
    val toast: String = "",
    val navigateToCart: Boolean = false
)

class PayPalCheckoutViewModel(
    private val paymentRepository: PaymentRepository,
    private val coordinator: PayPalWebCheckoutCoordinator,
    private val cartRepository: CartRepository
): ViewModel() {

    private val _payPalUiState = MutableStateFlow(PayPalUiState())
    val payPalUiState: StateFlow<PayPalUiState> = _payPalUiState

    init {
        viewModelScope.launch {
            coordinator.events.collect { event ->
                when (event) {
                    is PayPalWebCheckoutCoordinator.Event.Started -> {
                        _payPalUiState.update { it.copy(toast = "Opening PayPal...") }
                    }
                    is PayPalWebCheckoutCoordinator.Event.Approved -> {
                        capturePayPalOrder(event.orderId)
                    }
                    is PayPalWebCheckoutCoordinator.Event.Canceled -> {
                        _payPalUiState.update {
                            it.copy(
                                state = RequestState.Idle,
                                toast = "Payment cancelled.") }
                    }
                    is PayPalWebCheckoutCoordinator.Event.Failed -> {
                        _payPalUiState.update {
                            it.copy(
                                state = RequestState.Error(event.message),
                                toast = event.message )
                        }
                    }
                    is PayPalWebCheckoutCoordinator.Event.NoResult -> Unit
                }
            }
        }
    }

    fun startPayPalCheckout(activity: ComponentActivity, totalAmount: Double) {
        viewModelScope.launch {
            _payPalUiState.update { it.copy(state = RequestState.Loading, toast = "") }

            val amount = "%.2f".format(totalAmount)

            val orderId = paymentRepository.createPayPalOrder(
                currencyCode = "USD",
                amount = amount,
                referenceId = "burger-order-${System.currentTimeMillis()}"
            ).getOrElse { err ->
                Log.e("BurgerPayPalVm", "create order failed: ${err.message}", err )
                _payPalUiState.update { it.copy(
                    state = RequestState.Error("Failed to create paypal order"),
                    toast = "Failed to create paypal order") }
                return@launch
            }

            coordinator.startCheckout(activity, orderId)
        }
    }

    private fun capturePayPalOrder(orderId: String){

        viewModelScope.launch {
            Log.d("BurgerPayPalVm", "capture() started for orderId= $orderId")
            _payPalUiState.update { it.copy(state = RequestState.Loading) }

            val payPalResponse = paymentRepository.capturePayPalOrder(orderId).getOrElse { err ->
                Log.e("BurgerPayPalVm", "capture failed: ${err.message}", err )
                _payPalUiState.update { it.copy(
                    state = RequestState.Error("Failed to capture paypal order"),
                    toast = "Failed to capture paypal order") }
                return@launch
            }

            Log.d("BurgerPayPalVm", "capture response status=${payPalResponse.status}, captureId=${payPalResponse.captureId}")


            val result = PayPalPaymentResult(
                orderId,
                payPalResponse.status,
                payPalResponse.captureId
            )
            val isCompleted = payPalResponse.status.equals("COMPLETED", ignoreCase = true)


            if (!isCompleted) {
                _payPalUiState.update { it.copy(
                    state = RequestState.Error("Payment not completed. Status=${payPalResponse.status}"),
                    toast = "Payment status: ${payPalResponse.status}"
                ) }
                return@launch
            }
            when (val clearState = cartRepository.clearCart()) {
                is RequestState.Success -> {
                    Log.d("BurgerPayPalVm", "Cart cleared successfully after payment")
                    _payPalUiState.update {
                        it.copy(
                            state = RequestState.Success(result),
                            toast = "Payment completed and cart cleared",
                            navigateToCart = true
                        )
                    }
                }
                is RequestState.Error -> {
                    Log.e("BurgerPayPalVm", "Cart clear failed: ${clearState.message}")
                    _payPalUiState.update {
                        it.copy(
                            state = RequestState.Success(result),
                            toast = "Payment completed, but cart was not cleared",
                            navigateToCart = true
                        )
                    }
                }
                else -> {
                    _payPalUiState.update {
                        it.copy(
                            state = RequestState.Success(result),
                            toast = "Payment completed, and cart cleared",
                            navigateToCart = true
                        )
                    }
                }
            }
        }
    }

    fun consumeToast() {
        _payPalUiState.update { it.copy(toast = "") }
    }

    fun consumeNavigateToCart() {
        _payPalUiState.update { it.copy(navigateToCart = false) }
    }

    fun reset() {
        _payPalUiState.value = PayPalUiState()
    }
}