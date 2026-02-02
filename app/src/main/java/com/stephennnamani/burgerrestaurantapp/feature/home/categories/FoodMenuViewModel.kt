package com.stephennnamani.burgerrestaurantapp.feature.home.categories

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephennnamani.burgerrestaurantapp.core.data.domain.CustomerRepository
import com.stephennnamani.burgerrestaurantapp.core.data.domain.ProductRepository
import com.stephennnamani.burgerrestaurantapp.core.data.models.Product
import com.stephennnamani.burgerrestaurantapp.core.data.models.ProductCategoryUi
import com.stephennnamani.burgerrestaurantapp.feature.util.RequestState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FoodMenuViewModel(
    private val productRepository: ProductRepository,
    private val customerRepository: CustomerRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val category: String = checkNotNull(savedStateHandle.get<String>("category"))

    private val products: StateFlow<RequestState<List<Product>>> =
        productRepository.readProductsByCategory(category = category)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = RequestState.Loading
            )

    private val favouriteIds: StateFlow<RequestState<Set<String>>> =
        customerRepository.readFavouriteIdFlow()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = RequestState.Loading
            )

    val uiState: StateFlow<RequestState<List<ProductCategoryUi>>> =
        combine(products, favouriteIds) { productState, favState ->
            when (productState) {
                is RequestState.Loading -> RequestState.Loading
                is RequestState.Error -> RequestState.Error(productState.message)
                is RequestState.Success -> {
                    val favIds = (favState as? RequestState.Success)?.data.orEmpty()
                    val uiList = productState.data
                        .distinctBy { it.id }
                        .sortedByDescending { it.createdAt }
                        .map { product -> ProductCategoryUi(product, favIds.contains(product.id)) }

                    RequestState.Success(uiList)
                }

                RequestState.Idle -> RequestState.Idle
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RequestState.Idle
        )

    fun toggleFavourite(productId: String){
        viewModelScope.launch { customerRepository.toggleFavourite(productId) }
    }
}