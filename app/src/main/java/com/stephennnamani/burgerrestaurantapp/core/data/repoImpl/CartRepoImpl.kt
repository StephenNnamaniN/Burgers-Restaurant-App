package com.stephennnamani.burgerrestaurantapp.core.data.repoImpl

import com.stephennnamani.burgerrestaurantapp.core.data.domain.CartRepository
import com.stephennnamani.burgerrestaurantapp.core.data.domain.CustomerRepository
import com.stephennnamani.burgerrestaurantapp.core.data.domain.ProductRepository
import com.stephennnamani.burgerrestaurantapp.core.data.models.Cart
import com.stephennnamani.burgerrestaurantapp.core.data.models.CartItemUi
import com.stephennnamani.burgerrestaurantapp.core.data.models.Product
import com.stephennnamani.burgerrestaurantapp.feature.util.RequestState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

@OptIn(ExperimentalCoroutinesApi::class)
class CartRepoImpl(
    private val customerRepository: CustomerRepository,
    private val productRepository: ProductRepository
): CartRepository {

    override fun observerCartItems(): Flow<RequestState<List<CartItemUi>>> {
        return customerRepository.readCartFlow()
            .flatMapLatest { cartState ->
                when ( cartState) {
                    is RequestState.Idle -> flowOf(RequestState.Idle)
                    is RequestState.Loading -> flowOf(RequestState.Loading)
                    is RequestState.Error -> flowOf(RequestState.Error(cartState.message))
                    is RequestState.Success -> {
                        val cartList = cartState.data
                        if (cartList.isEmpty()) {
                            flowOf(RequestState.Success(emptyList()))
                        } else {
                            val flows: List<Flow<Pair<Cart, RequestState<Product>>>> =
                                cartList.map { cart ->
                                    productRepository.readProductById(cart.productId)
                                        .map { productState -> cart to productState }
                                }
                            combine(flows) { pairs ->
                                val items = pairs.mapNotNull { (cart, productState)  ->
                                    when (productState) {
                                        is RequestState.Success -> CartItemUi(
                                            product = productState.data,
                                            quantity = cart.quantity
                                        )
                                        else -> null
                                    }
                                }
                                RequestState.Success(items) as RequestState<List<CartItemUi>>
                            }
                                .onStart {emit(RequestState.Loading)}
                                .catch { e -> emit(RequestState.Error(e.message ?: "Failed to build cart items.")) }
                        }

                    }
                }
            }
    }

    override suspend fun increment(
        productId: String,
        productTitle: String?
    ): RequestState<Unit> {
        val title = productTitle ?: ""
        return customerRepository.addToCart(productId, title, quantityToAdd = 1)
    }

    override suspend fun decrement(productId: String): RequestState<Unit> {
        return customerRepository.removeFromCart(productId, quantityToRemove = 1)
    }

    override suspend fun delete(productId: String): RequestState<Unit> {
        return customerRepository.deleteCartItem(productId)
    }

    override suspend fun setQuantity(
        productId: String,
        quantity: Int
    ): RequestState<Unit> {
        return customerRepository.setCartQuantity(productId, quantity)
    }
}