package com.stephennnamani.burgerrestaurantapp.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.stephennnamani.burgerrestaurantapp.core.data.domain.CustomerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val customerRepository: CustomerRepository,
): ViewModel() {
    fun createCustomer(
        user: FirebaseUser,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ){
        viewModelScope.launch(Dispatchers.IO) {
            customerRepository.createCustomer(
                user = user,
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }
}