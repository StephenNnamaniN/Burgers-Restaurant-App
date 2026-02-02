package com.stephennnamani.burgerrestaurantapp.feature.product_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephennnamani.burgerrestaurantapp.core.data.domain.CustomerRepository
import com.stephennnamani.burgerrestaurantapp.core.data.domain.ProductRepository
import com.stephennnamani.burgerrestaurantapp.core.data.models.Product
import com.stephennnamani.burgerrestaurantapp.feature.util.RequestState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select

data class ProductDetailsUiState(
    val showSuggestedDialog: Boolean = false,
    val suggestedProducts: RequestState<List<Product>> = RequestState.Idle,
    val isFavourite: Boolean = false,
    val actionMessage: String? = null,
    val favouriteIds: Set<String> = emptySet(),
    val addedCartTotal: Double = 0.0,
    val addedSuggestedIds: Set<String> = emptySet(),
    val suggestedQuantities: Map<String, Int> = emptyMap()
)

sealed interface ProductDetailsEvent {
    data class NavigateToCheckout(val amount: Double?): ProductDetailsEvent
    data class showMessage(val message: String): ProductDetailsEvent
}
class ProductDetailsViewModel(
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle,
    private val customerRepository: CustomerRepository
): ViewModel() {

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity

    private val _events = MutableSharedFlow<ProductDetailsEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()
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
    fun incrementSuggested(productId: String) {
        _baseUiState.update { state ->
            val current = state.suggestedQuantities[productId] ?: 0
            state.copy(
                suggestedQuantities = state.suggestedQuantities +
                        (productId to (current + 1)
                            .coerceAtMost(99))

            )
        }
    }

    fun decrementSuggested(productId: String) {
        _baseUiState.update { state ->
            val current = state.suggestedQuantities[productId] ?: 0
            val next = (current - 1) .coerceAtLeast(0)
            val updatedMap =
                if (next == 0) state.suggestedQuantities - productId
                else state.suggestedQuantities + (productId to next)
            state.copy(suggestedQuantities = updatedMap)
        }
    }

    // Stubs
    fun addToCart(){
        val qty = quantity.value
        val products = product.value.getSuccessDataOrNull() ?: return

        viewModelScope.launch {
            _baseUiState.update { it.copy(actionMessage = null) }
            when (
                customerRepository.addToCart(
                    productId = products.id,
                    productTitle = products.title,
                    quantityToAdd = qty
                )
            ){
                is RequestState.Success -> {
                    val initialTotal = products.price *qty
                    _baseUiState.update { it.copy(
                        showSuggestedDialog = true,
                        addedCartTotal = initialTotal
                    ) }
                    _suggestedEnabled.value = true
                }
                is RequestState.Error -> {
                    _baseUiState.update { it.copy(actionMessage = "Failed to add to cart.") }
                }
                else -> Unit
            }
        }
    }

    fun confirmSuggestedSelectionToCart(
        onDone: () -> Unit
    ){
        val state = _baseUiState.value
        val selected = state.suggestedQuantities.filterValues { it > 0 }
        if(selected.isEmpty()) {
            _baseUiState.update {
                it.copy(
                    suggestedQuantities = emptyMap(),
                    showSuggestedDialog = false,
                    actionMessage = null
                )
            }
            onDone()
            return
        }
        val products = (suggestedProducts.value as? RequestState.Success)?.data.orEmpty()
        val productById = products.associateBy { it.id }

        viewModelScope.launch {
            _baseUiState.update { it.copy(actionMessage = null) }
            val errors = mutableListOf<String>()

            selected.forEach { (productId, qty)  ->
                val product = productById[productId]

                if (product == null) {
                    errors.add("Could not resolve suggested product: $productId")
                    return@forEach
                }

                val result = customerRepository.addToCart(
                    productId = product.id,
                    productTitle = product.title,
                    quantityToAdd = qty
                )
                if (result is RequestState.Error) {
                    errors.add("Failed to add ${product.title}: ${result.message}")
                }
                if (errors.isNotEmpty()){
                    _baseUiState.update {
                        it.copy(
                            actionMessage = errors.joinToString { "\n" },
                            showSuggestedDialog = true
                        )
                    }
                    return@launch
                }
                _baseUiState.update {
                    it.copy(
                        suggestedQuantities = emptyMap(),
                        showSuggestedDialog = false,
                        actionMessage = null
                    )
                }
                onDone()
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
    fun buyNow(){
        val product = product.value.getSuccessDataOrNull()
        if (product == null){
            _events.tryEmit(ProductDetailsEvent.showMessage("Product not available yet."))
        }

        val qty = quantity.value.coerceAtLeast(1)
        val total = (product?.price?.times(qty))

        if (total != null){
            if (total <= 0.0) {
                _events.tryEmit(ProductDetailsEvent.showMessage("Invalid total amount."))
                return
            }
        }
        _events.tryEmit(ProductDetailsEvent.NavigateToCheckout(amount = total))
    }
}