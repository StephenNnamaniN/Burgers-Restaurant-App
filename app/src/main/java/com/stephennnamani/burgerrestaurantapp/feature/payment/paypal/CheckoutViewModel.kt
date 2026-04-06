package com.stephennnamani.burgerrestaurantapp.feature.payment.paypal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephennnamani.burgerrestaurantapp.core.data.domain.CustomerRepository
import com.stephennnamani.burgerrestaurantapp.feature.payment.DeliveryFormatter
import com.stephennnamani.burgerrestaurantapp.feature.util.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CheckoutUiState(
    val delivery: RequestState<DeliveryFormatter.DeliveryUi> = RequestState.Loading
)

class CheckoutViewModel(
    private val customerRepository: CustomerRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState

    init {
        observeDelivery()
    }


    private fun observeDelivery() {
        viewModelScope.launch {
            customerRepository.readCustomerFlow().collect { state ->
                when {
                    state.isSuccess() -> _uiState.update {
                        it.copy(delivery = RequestState.Success(DeliveryFormatter.from(state.getSuccessData())))
                    }
                    state.isError() -> {
                        Log.e("BurgerCheckout", "Delivery reading failed: ${state.getErrorMessage()}")
                        _uiState.update { it.copy(delivery = RequestState.Success(DeliveryFormatter.from(null))) }
                    }
                    state.isLoading() -> _uiState.update { it.copy(delivery = RequestState.Loading) }
                    else -> Unit
                }
            }
        }
    }
}