package com.stephennnamani.burgerrestaurantapp.core.data.domain

import com.stephennnamani.burgerrestaurantapp.core.data.models.Product
import com.stephennnamani.burgerrestaurantapp.feature.util.RequestState
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun readNewProducts(): Flow<RequestState<List<Product>>>
    fun readDiscountedProducts(): Flow<RequestState<List<Product>>>
    fun readPopularProducts(): Flow<RequestState<List<Product>>>
    fun readProductsByCategory(category: String): Flow<RequestState<List<Product>>>
}