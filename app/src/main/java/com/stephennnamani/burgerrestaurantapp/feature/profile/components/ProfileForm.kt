package com.stephennnamani.burgerrestaurantapp.feature.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.stephennnamani.burgerrestaurantapp.core.data.models.Country
import com.stephennnamani.burgerrestaurantapp.feature.component.BurgerSelectTextField
import com.stephennnamani.burgerrestaurantapp.feature.component.BurgerTextField


@Composable
fun ProfileForm(
    modifier: Modifier = Modifier,
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    email: String,
    country: Country?,
    onCountrySelect: () -> Unit,
    city: String?,
    onCityChange: (String) -> Unit,
    postalCode: Int?,
    onPostalCodeChange: (Int?) -> Unit,
    address: String?,
    onAddressChange: (String) -> Unit,
    phoneNumber: String?,
    onPhoneNumberChange: (String) -> Unit,

){
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BurgerTextField(
            value = firstName,
            onValueChange = onFirstNameChange,
            placeholder = "First Name",
            error = firstName.length !in 3..50
        )
        BurgerTextField(
            value = lastName,
            onValueChange = onLastNameChange,
            placeholder = "Last Name",
            error = firstName.length !in 3..50
        )
        BurgerTextField(
            value = email,
            onValueChange = {},
            placeholder = "Email",
            enabled = false
        )
        BurgerSelectTextField(
            modifier = Modifier.fillMaxWidth(),
            text = country?.name ?: "",
            iconUrl = country?.flagUrl,
            onClick = onCountrySelect,
            placeholder = "Country"
        )
        BurgerTextField(
            value = city ?: "",
            onValueChange = onCityChange,
            placeholder = "City",
            error = city?.length !in 3..50
        )
        BurgerTextField(
            value = "${postalCode ?: ""}",
            onValueChange = { onPostalCodeChange(it.toIntOrNull()) },
            placeholder = "Postal code",
            error = postalCode == null || firstName.length !in 3..8,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            )
        )
        BurgerTextField(
            value = address ?: "",
            onValueChange = onAddressChange,
            placeholder = "Address",
            error = address?.length !in 3..50,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BurgerSelectTextField(
                modifier = Modifier.width(120.dp),
                text = if (country?.dialCode != null) "+${country.dialCode}" else "+-",
                iconUrl = country?.flagUrl,
                onClick = onCountrySelect,
                placeholder = "+-"
            )
            Spacer(modifier = Modifier.width(12.dp))
            BurgerTextField(
                modifier = Modifier.weight(1f),
                value = phoneNumber ?: "",
                onValueChange = onPhoneNumberChange,
                placeholder = "Phone Number",
                error = phoneNumber.toString().length !in 3..30,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
        }

    }
}