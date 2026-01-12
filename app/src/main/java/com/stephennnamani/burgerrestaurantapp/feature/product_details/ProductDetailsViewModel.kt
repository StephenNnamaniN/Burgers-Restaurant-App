package com.stephennnamani.burgerrestaurantapp.feature.product_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephennnamani.burgerrestaurantapp.core.data.domain.CustomerRepository
import com.stephennnamani.burgerrestaurantapp.core.data.domain.ProductRepository
import com.stephennnamani.burgerrestaurantapp.core.data.models.Product
import com.stephennnamani.burgerrestaurantapp.feature.util.RequestState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductDetailsUiState(
    val showSuggestedDialog: Boolean = false,
    val suggestedProducts: RequestState<List<Product>> = RequestState.Idle,
    val isFavourite: Boolean = false,
    val actionMessage: String? = null,
    val favouriteIds: Set<String> = emptySet(),
    val addedCartTotal: Double = 0.0,
    val addedSuggestedIds: Set<String> = emptySet()
)
class ProductDetailsViewModel(
    private val productRepository: ProductRepository,
    private val savedStateHandle: SavedStateHandle,
    private val customerRepository: CustomerRepository
): ViewModel() {

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity
    private val _suggestedEnabled = MutableStateFlow(false)
    private val productId: String = savedStateHandle.get<String>("id").orEmpty()

    @OptIn(ExperimentalCoroutinesApi::class)
    val product: StateFlow<RequestState<Product>> =
        productRepository.readProductById(productId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RequestState.Loading
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val suggestedProducts: StateFlow<RequestState<List<Product>>> =
        _suggestedEnabled
            .flatMapLatest { enabled ->
                if (!enabled) flowOf(RequestState.Idle)
                else productRepository.readPopularProducts()
                    .onStart { emit(RequestState.Loading) }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = RequestState.Idle
            )

    val favouriteIds: StateFlow<Set<String>> =
        customerRepository.readFavouriteIdFlow()
            .map { state -> state.getSuccessDataOrNull().orEmpty()
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptySet()
            )

    private val isFavourite: StateFlow<Boolean> =
        favouriteIds
            .map { ids -> ids.contains(productId) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false
            )

    private val _baseUiState = MutableStateFlow(
        ProductDetailsUiState(
            showSuggestedDialog = false,
            addedSuggestedIds = emptySet(),
            addedCartTotal = 0.0,
            actionMessage = null
        )
    )
    val uiState: StateFlow<ProductDetailsUiState> =
        combine(
            _baseUiState,
            suggestedProducts,
            favouriteIds,
            isFavourite
        ) { base, suggestions, favIds, isFav ->
            base.copy(
                suggestedProducts = suggestions,
                favouriteIds = favIds,
                isFavourite = isFav
            )
        }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = _baseUiState.value.copy(
                    suggestedProducts = RequestState.Idle,
                    favouriteIds = emptySet(),
                    isFavourite = true
                )
            )


    fun productQtyIncrement() {
        _quantity.update { current -> (current + 1).coerceAtMost(99) }
    }
    fun productQtyDecrement() {
        _quantity.update { current -> (current - 1).coerceAtLeast(1) }
    }

    // Stubs
    fun addToCart(){
        val qty = quantity.value
        val products = product.value.getSuccessDataOrNull() ?: return

        viewModelScope.launch {
            _baseUiState.update { it.copy(actionMessage = null) }
            when (
                customerRepository.addToCart(
                    products.id,
                    products.title,
                    qty
                )
            ){
                is RequestState.Success -> {
                    val initialTotal = products.price * qty
                    _baseUiState.update { it.copy(
                        showSuggestedDialog = true,
                        addedCartTotal = initialTotal
                    ) }
                    _suggestedEnabled .value = true
                }
                is RequestState.Error -> {
                    _baseUiState.update { it.copy(actionMessage = "Failed to add to cart.") }
                }
                else -> Unit
            }
        }
    }
    fun addSuggestedToCart(product: Product, quantityToAdd: Int = 1){
        if (_baseUiState.value.addedSuggestedIds.contains(product.id)) return

        viewModelScope.launch {
            when (
                customerRepository.addToCart(
                    productId = product.id,
                    productTitle = product.title,
                    quantityToAdd = quantityToAdd
                )
            ){
                is RequestState.Success -> {
                    _baseUiState.update { state ->
                        state.copy(
                            addedSuggestedIds = state.addedSuggestedIds + product.id,
                            addedCartTotal = state.addedCartTotal + (product.price * quantityToAdd)
                        )
                    }
                }
                else -> Unit
            }
        }
    }

    fun removeSuggestedFromCart(product: Product, quantityToRemove: Int = 1){
        val wasAdded = _baseUiState.value.addedSuggestedIds.contains(product.id)
        if (!wasAdded) return

        viewModelScope.launch {
            when (
                customerRepository.removeFromCart(
                    productId = product.id,
                    quantityToRemove = quantityToRemove)
            ){
                is RequestState.Success -> {
                    _baseUiState.update { state ->
                        state.copy(
                            addedSuggestedIds = state.addedSuggestedIds - product.id,
                            addedCartTotal = (state.addedCartTotal - (product.price * quantityToRemove)
                        ).coerceAtLeast(0.0))
                    }
                }
                else -> Unit
            }
        }
    }
    fun toggleFavourite(){
        viewModelScope.launch {
            customerRepository.toggleFavourite(productId)
        }
    }
    fun dismissSuggestedDialog(){
        _baseUiState.update { it.copy(showSuggestedDialog = false, actionMessage = null) }
        _suggestedEnabled.value = false
    }
    fun buyNow(){}
}