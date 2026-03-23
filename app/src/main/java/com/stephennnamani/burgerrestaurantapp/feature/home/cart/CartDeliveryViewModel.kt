package com.stephennnamani.burgerrestaurantapp.feature.home.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephennnamani.burgerrestaurantapp.core.data.domain.CustomerRepository
import com.stephennnamani.burgerrestaurantapp.feature.payment.DeliveryFormatter
import com.stephennnamani.burgerrestaurantapp.feature.util.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartDeliveryViewModel(
    private val customerRepository: CustomerRepository
): ViewModel() {

    private val _delivery = MutableStateFlow<RequestState<DeliveryFormatter.DeliveryUi>>(RequestState.Loading)
    val delivery: StateFlow<RequestState<DeliveryFormatter.DeliveryUi>> = _delivery

    init {
        viewModelScope.launch {
            customerRepository.readCustomerFlow().collect { state ->
                when {
                    state.isSuccess() -> _delivery.value =
                        RequestState.Success(DeliveryFormatter.from(state.getSuccessData()))
                    state.isError() -> _delivery.value =
                        RequestState.Success(DeliveryFormatter.from(null))
                    state.isLoading() -> _delivery.value = RequestState.Loading
                    else -> Unit
                }
            }
        }
    }
}