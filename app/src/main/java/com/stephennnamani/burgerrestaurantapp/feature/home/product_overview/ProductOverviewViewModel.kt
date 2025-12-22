package com.stephennnamani.burgerrestaurantapp.feature.home.product_overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephennnamani.burgerrestaurantapp.core.data.domain.ProductRepository
import com.stephennnamani.burgerrestaurantapp.core.data.models.ProductCategory
import com.stephennnamani.burgerrestaurantapp.feature.util.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class ProductOverviewViewModel(
    private val productRepository: ProductRepository
): ViewModel() {
    val newProducts = productRepository.readNewProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RequestState.Loading
        )

    val popularProducts = productRepository.readPopularProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RequestState.Loading
        )

    val discountedProducts = productRepository.readDiscountedProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RequestState.Loading
        )

    private  val _selectedCategory = MutableStateFlow<ProductCategory?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    val categoryProducts = selectedCategory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RequestState.Idle
        )

    fun selectedCategory(category: ProductCategory){
        _selectedCategory.value = category
    }

    fun clearCategory() {
        _selectedCategory.value = null
    }
}