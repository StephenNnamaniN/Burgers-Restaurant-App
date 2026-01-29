package com.stephennnamani.burgerrestaurantapp.core.data.domain

import com.stephennnamani.burgerrestaurantapp.core.data.models.CartItemUi
import com.stephennnamani.burgerrestaurantapp.feature.util.RequestState
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun observerCartItems(): Flow<RequestState<List<CartItemUi>>>
    suspend fun increment(productId: String, productTitle: String? = null): RequestState<Unit>
    suspend fun decrement(productId: String): RequestState<Unit>
    suspend fun delete(productId: String): RequestState<Unit>
    suspend fun setQuantity(productId: String, quantity: Int): RequestState<Unit>
}