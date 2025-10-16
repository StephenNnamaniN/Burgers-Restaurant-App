package com.stephennnamani.burgerrestaurantapp.feature.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephennnamani.burgerrestaurantapp.core.data.domain.CountryRepository
import com.stephennnamani.burgerrestaurantapp.core.data.domain.CustomerRepository
import com.stephennnamani.burgerrestaurantapp.core.data.models.Country
import com.stephennnamani.burgerrestaurantapp.core.data.models.Customer
import com.stephennnamani.burgerrestaurantapp.core.data.models.PhoneNumber
import com.stephennnamani.burgerrestaurantapp.feature.util.RequestState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

data class ProfileScreenState(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val city: String? = null,
    val address: String? = null,
    val country: Country? = null,
    val postalCode: Int? = null,
    val phoneNumber: PhoneNumber? = null,
    val profilePictureUrl: String? = null
)
class ProfileViewModel(
    private val customerRepository: CustomerRepository,
    private val countryRepository: CountryRepository
): ViewModel() {
    var screenReady: RequestState<Unit> by mutableStateOf(RequestState.Loading)
    var screenState: ProfileScreenState by mutableStateOf(ProfileScreenState())
        private set

    var countriesState by mutableStateOf<RequestState<List<Country>>>(RequestState.Loading)
        private set

    private var countries: List<Country> = emptyList()

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
        viewModelScope.launch { loadCountries() }
    }

    private fun loadCountries() = viewModelScope.launch   {
        countryRepository.fetchCountries()
            .onStart { countriesState = RequestState.Loading }
            .collect { state ->
                countriesState = state
                if (state is RequestState.Success){
                    countries = state.data

                    screenState.phoneNumber?.dialCode?.let { dial ->
                        state.data.firstOrNull { it.dialCode == dial }?. let { match ->
                            screenState = screenState.copy(country = match)
                        }
                    }
                }
            }
    }
    private suspend fun observeCustomer(){
        customerRepository.readCustomerFlow().collectLatest { data  ->
            when {
                data.isSuccess() -> {
                    val fetched = data.getSuccessData()
                    val dial = fetched.phoneNumber?.dialCode

                    val mappedCountry =
                        if (dial != null && countries.isNotEmpty())
                            countries.firstOrNull() {it.dialCode == dial }
                        else screenState.country


                    screenState = ProfileScreenState(
                        id = fetched.id,
                        firstName = fetched.firstName,
                        lastName = fetched.lastName,
                        email = fetched.email,
                        city = fetched.city,
                        postalCode = fetched.postalCode,
                        address = fetched.address,
                        phoneNumber = fetched.phoneNumber,
                        country = mappedCountry,
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

    fun updateCountry(value: Country) {
        screenState = screenState.copy(
            country = value,
            phoneNumber = screenState.phoneNumber?.copy(
                dialCode = value.dialCode
            ) ?: PhoneNumber(dialCode = value.dialCode, number = screenState.phoneNumber?.number ?: "")
        )
    }

    fun updateCustomer(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ){
        viewModelScope.launch {
            val persistedCountry = screenState.country?.let {
                Country(
                    name = it.name,
                    code = it.code,
                    dialCode = it.dialCode,
                    flagUrl = it.flagUrl
                )
            }
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
                    country = persistedCountry,
                    profilePictureUrl = screenState.profilePictureUrl
                ),
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }


}