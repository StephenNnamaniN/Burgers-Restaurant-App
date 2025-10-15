package com.stephennnamani.burgerrestaurantapp.feature.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephennnamani.burgerrestaurantapp.core.data.domain.CustomerRepository
import com.stephennnamani.burgerrestaurantapp.core.data.models.Customer
import com.stephennnamani.burgerrestaurantapp.core.data.models.PhoneNumber
import com.stephennnamani.burgerrestaurantapp.feature.util.RequestState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class ProfileScreenState(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val city: String? = null,
    val address: String? = null,
    val postalCode: Int? = null,
    val phoneNumber: PhoneNumber? = null,
    val profilePictureUrl: String? = null
)
class ProfileViewModel(
    private val customerRepository: CustomerRepository
): ViewModel() {
    var screenReady: RequestState<Unit> by mutableStateOf(RequestState.Loading)
    var screenState: ProfileScreenState by mutableStateOf(ProfileScreenState())
        private set

    val isFormValid: Boolean
        get() = with(screenState) {
            (firstName.length in 3..50) &&
                    (lastName.length in 3..50) &&
                    (city?.length in 3..50) &&
                    (postalCode != null || postalCode.toString().length in 3..8) &&
                    (address?.length in 3..50) &&
                    (phoneNumber?.number?.length in 5..30)
        }
    init {
        viewModelScope.launch { observeCustomer() }
    }

    private suspend fun observeCustomer(){
        customerRepository.readCustomerFlow().collectLatest { data  ->
            when {
                data.isSuccess() -> {
                    val fetched = data.getSuccessData()
                    val dial = fetched.phoneNumber?.dialCode

                    screenState = ProfileScreenState(
                        id = fetched.id,
                        firstName = fetched.firstName,
                        lastName = fetched.lastName,
                        email = fetched.email,
                        city = fetched.city,
                        postalCode = fetched.postalCode,
                        address = fetched.address,
                        phoneNumber = fetched.phoneNumber,
                        profilePictureUrl = fetched.profilePictureUrl
                    )
                    screenReady = RequestState.Success(Unit)
                }
                data.isError() -> {
                    screenReady = RequestState.Error(data.getErrorMessage())
                }
                else -> Unit
            }
        }
    }
    fun updateFirstName(value: String){
        screenState = screenState.copy(firstName = value)
    }
    fun updateLastName(value: String){
        screenState = screenState.copy(lastName = value)
    }
    fun updateCity(value: String){
        screenState = screenState.copy(city = value)
    }
    fun updateAddress(value: String){
        screenState = screenState.copy(address = value)
    }
    fun updatePostalCode(value: Int?){
        screenState = screenState.copy(postalCode = value)
    }
    fun updatePhoneNumber(value: String){
        screenState = screenState.copy(
            phoneNumber = PhoneNumber(
                dialCode = screenState.phoneNumber?.dialCode ?: 0,
                number = value,
            )
        )
    }

    fun updateCustomer(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ){
        viewModelScope.launch {
            customerRepository.updateCustomer(
                customer = Customer(
                    id = screenState.id,
                    firstName = screenState.firstName,
                    lastName = screenState.lastName,
                    email = screenState.email,
                    city = screenState.city,
                    postalCode = screenState.postalCode,
                    address = screenState.address,
                    phoneNumber = screenState.phoneNumber,
                    profilePictureUrl = screenState.profilePictureUrl
                ),
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }


}