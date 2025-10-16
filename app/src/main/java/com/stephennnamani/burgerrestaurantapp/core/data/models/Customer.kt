package com.stephennnamani.burgerrestaurantapp.core.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val city: String? = null,
    val address: String? = null,
    val postalCode: Int? = null,
    val phoneNumber: PhoneNumber? = null,
    val country: Country? = null,
    val isAdmin: Boolean = false,
    val profilePictureUrl: String?
)

@Serializable
data class PhoneNumber(
    val dialCode: Int,
    val number: String
)
