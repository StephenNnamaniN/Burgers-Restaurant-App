package com.stephennnamani.burgerrestaurantapp.core.data.domain

import android.net.Uri
import com.stephennnamani.burgerrestaurantapp.core.data.models.Product
import com.stephennnamani.burgerrestaurantapp.feature.util.RequestState
import kotlinx.coroutines.flow.Flow

interface AdminRepository {
    fun getCurrentUserId(): String?
    suspend fun uploadProductImageToStorage(imageUri: Uri): Result<String>
    suspend fun deleteProductImageFromStorage(downloadUrl: String): Result<Unit>

    suspend fun createNewProduct(product: Product)

    suspend fun updateProductThumbnail(
        productId: String,
        downloadUrl: String
    ): Result<Unit>

    fun readLastTenProducts(): Flow<RequestState<List<Product>>>
    suspend fun readProductById(id: String): RequestState<Product>

    suspend fun updateProduct(
        product: Product
    ): Result<Unit>

    suspend fun deleteProduct(
        productId: String
    ): Result<Unit>

    fun searchProductByTitle(searchQuery: String): Flow<RequestState<List<Product>>>
}