package com.stephennnamani.burgerrestaurantapp.feature.home.product_overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephennnamani.burgerrestaurantapp.core.data.domain.ProductRepository
import com.stephennnamani.burgerrestaurantapp.core.data.models.Product
import com.stephennnamani.burgerrestaurantapp.core.data.models.ProductCategory
import com.stephennnamani.burgerrestaurantapp.feature.util.RequestState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

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
    private val heroCandidate: StateFlow<List<Product>> =
        newProducts.map { state ->
            state.getSuccessDataOrNull()
                ?.sortedByDescending { it.createdAt }
                ?.take(3)
                ?: emptyList()
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val heroPaused = selectedCategory
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )
    private val heroIndex = MutableStateFlow(0)

    val heroProduct: StateFlow<Product?> =
        combine(heroCandidate, heroIndex) {list, index ->
            list.getOrNull(index)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    init {
        viewModelScope.launch {
            combine(heroCandidate, heroPaused) { list, paused -> list to paused}
                .collectLatest { (list, paused) ->
                    heroIndex.value = 0
                    if (paused || list.size <= 1) return@collectLatest

                    while (isActive && !heroPaused.value){
                        delay(5000)
                        heroIndex.value = (heroIndex.value + 1) % list.size
                    }
                }

        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val categoryProducts = selectedCategory
        .flatMapLatest { category ->
            if (category == null){
                flowOf(RequestState.Idle)
            } else {
                productRepository.readProductsByCategory(category.title)
            }
        }
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