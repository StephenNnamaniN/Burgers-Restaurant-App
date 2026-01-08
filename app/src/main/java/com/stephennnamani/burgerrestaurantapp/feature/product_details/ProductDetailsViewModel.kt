package com.stephennnamani.burgerrestaurantapp.feature.product_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephennnamani.burgerrestaurantapp.core.data.domain.ProductRepository
import com.stephennnamani.burgerrestaurantapp.core.data.models.Product
import com.stephennnamani.burgerrestaurantapp.feature.util.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailsViewModel(
    private val productRepository: ProductRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _productState = MutableStateFlow<RequestState<Product>>(RequestState.Loading)
    val productState: StateFlow<RequestState<Product>> = _productState

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity

    init {
        load(savedStateHandle.get<String>("id") ?: "")
    }

    fun load(productId: String) {
        viewModelScope.launch {
            productRepository.readProductById(productId).collectLatest { state ->
                _productState.value = state
            }
        }
    }

    fun productQtyIncrement() {
        _quantity.update { current -> (current + 1).coerceAtMost(99) }
    }
    fun productQtyDecrement() {
        _quantity.update { current -> (current - 1).coerceAtLeast(1) }
    }

    // Stubs
    fun addToCart(){}
    fun buyNow(){}
    fun toggleFavourite(){}
}