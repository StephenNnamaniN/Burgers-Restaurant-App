package com.stephennnamani.burgerrestaurantapp.feature.payment

import com.stephennnamani.burgerrestaurantapp.core.data.models.Customer

object DeliveryFormatter {

    data class DeliveryUi(
        val addressLine: String,
        val postcode: String?
    )

    fun from(customer: Customer?): DeliveryUi {
        if (customer == null) return DeliveryUi("Unknown", "Unknown")

        val address = customer.address?.trim().orEmpty()
        val city = customer.city?.trim().orEmpty()

        val addressLine = when {
            address.isNotBlank() && city.isNotBlank() -> "$address, $city"
            address.isNotBlank() -> address
            city.isNotBlank() -> city
            else -> "Unknown"
        }

        val postcode = customer.postalCode?.toString()?.trim().takeUnless { it.isNullOrBlank() }

        return DeliveryUi(addressLine, postcode)
    }
}