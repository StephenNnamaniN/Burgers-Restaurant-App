package com.stephennnamani.burgerrestaurantapp.core.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Country(
    val code: String,
    val name: String,
    val dialCode : Int,
    val flagUrl: String?
)

@Serializable
data class RestCountriesDto(
    val name: Name? = null,
    val idd: Idd? = null,
    val flags: Flags? = null,
    val cca2: String? = null
)

@Serializable
data class Name(val common: String? = null)

@Serializable
data class Idd(
    val root: String? = null,
    val suffixes: List<String>? = emptyList()
)

@Serializable
data class Flags(
    val png: String? = null,
    val svg: String? = null,
    @SerialName("alt") val alt: String? = null
)

fun RestCountriesDto.toCountryOrNull(): Country? {
    val displayName = name?.common?.takeIf { it.isNotBlank() } ?: return null
    val code2 = cca2?.takeIf { it.isNotBlank() } ?: return null

    val root = idd?.root?.takeIf { it.startsWith("+") } ?: "+0"
    val dialText = when {
        idd?.suffixes?.size == 1 && idd.suffixes.firstOrNull()?.isNotBlank() == true
            -> root + idd.suffixes.first()
        else -> root
    }
    val dialInt = dialText.filter { it.isDigit() }.toIntOrNull() ?: return null

    return Country(
        name = displayName,
        dialCode = dialInt,
        code = code2,
        flagUrl = (flags?.png ?: flags?.svg)
    )
}