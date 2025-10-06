package com.stephennnamani.burgerrestaurantapp.core.data.domain

import com.google.firebase.auth.FirebaseUser
import com.stephennnamani.burgerrestaurantapp.feature.util.RequestState

interface CustomerRepository {
    fun getCurrentUserId(): String?

    suspend fun createCustomer(
        user: FirebaseUser,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    suspend fun signOut(): RequestState<Unit>
}