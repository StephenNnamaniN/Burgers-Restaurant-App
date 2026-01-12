package com.stephennnamani.burgerrestaurantapp.core.data.domain

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.stephennnamani.burgerrestaurantapp.core.data.models.Customer
import com.stephennnamani.burgerrestaurantapp.feature.util.RequestState
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    fun getCurrentUserId(): String?

    suspend fun createCustomer(
        user: FirebaseUser,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    fun readCustomerFlow(): Flow<RequestState<Customer>>
    suspend fun updateCustomer(
        customer: Customer,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    suspend fun updateProfilePictureUrl(url: String): RequestState<Unit>
    suspend fun uploadProfilePhoto(localUrl: Uri, onProgress: (Float) -> Unit): RequestState<String>
    suspend fun signOut(): RequestState<Unit>


    //Cart functions
    suspend fun addToCart(
        productId: String,
        quantityToAdd: Int
    ): RequestState<Unit>

    suspend fun toggleFavourite(productId: String): RequestState<Boolean>
    suspend fun isFavourite(productId: String): RequestState<Boolean>
}