package com.stephennnamani.burgerrestaurantapp.feature.home.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephennnamani.burgerrestaurantapp.core.data.domain.CartRepository
import com.stephennnamani.burgerrestaurantapp.core.data.models.CartItemUi
import com.stephennnamani.burgerrestaurantapp.feature.util.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CartUiState(
    val cartItems: RequestState<List<CartItemUi>> = RequestState.Loading,
    val promoCode: String = "",
    val deliveryFee: Double = 4.0,
    val vatPercent: Double = 0.20
)
class CartViewModel(
    private val cartRepository: CartRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState

    fun onPromoCodeChanged(value: String) {
        _uiState.update { it.copy(promoCode = value) }
    }

    fun increment(cartItem: CartItemUi) = viewModelScope.launch {
        cartRepository.increment(productId = cartItem.product.id, productTitle = cartItem.product.title)
    }

    fun decrement(productId: String) = viewModelScope.launch {
        cartRepository.decrement(productId)
    }

    fun delete(productId: String) = viewModelScope.launch {
        cartRepository.delete(productId)
    }

    fun subTotal(items: List<CartItemUi>): Double =
        items.sumOf { it.product.price * it.quantity }

    fun vatAmount(subTotal: Double): Double =
        subTotal * _uiState.value.vatPercent

    fun totalAmount(subTotal: Double): Double =
        subTotal + _uiState.value.deliveryFee + vatAmount(subTotal)
}