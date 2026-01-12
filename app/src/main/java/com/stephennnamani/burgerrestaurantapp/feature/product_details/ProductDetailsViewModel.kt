package com.stephennnamani.burgerrestaurantapp.feature.product_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephennnamani.burgerrestaurantapp.core.data.domain.ProductRepository
import com.stephennnamani.burgerrestaurantapp.core.data.models.Product
import com.stephennnamani.burgerrestaurantapp.feature.util.RequestState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailsViewModel(
    private val productRepository: ProductRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity

    @OptIn(ExperimentalCoroutinesApi::class)
    val product = savedStateHandle.getStateFlow("id", "")
        .flatMapLatest { id ->
            productRepository.readProductById(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RequestState.Loading
        )

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