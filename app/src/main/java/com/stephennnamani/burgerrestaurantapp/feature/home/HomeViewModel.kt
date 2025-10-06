package com.stephennnamani.burgerrestaurantapp.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephennnamani.burgerrestaurantapp.core.data.domain.CustomerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val customerRepository: CustomerRepository
): ViewModel() {
    fun signOut(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ){
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO){
                customerRepository.signOut()
            }
            if (result.isSuccess()){
                onSuccess()
            } else if (result.isError()){
                onError(result.getErrorMessage())
            }
        }
    }
}