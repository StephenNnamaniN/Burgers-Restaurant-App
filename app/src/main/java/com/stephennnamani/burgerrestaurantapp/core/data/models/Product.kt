package com.stephennnamani.burgerrestaurantapp.core.data.models

import com.stephennnamani.burgerrestaurantapp.ui.theme.Resources

data class Product(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val allergyAdvice: String,
    val energyValue: Int?,
    val ingredients: String,
    val price: Double,
)

enum class ProductCategory(
    val title: String,
    val icon: Int
){
    Burgers(
        title = "Burgers",
        icon = Resources.Icon.Burgers
    ),
    Nuggets(
    title = "Nuggets",
    icon = Resources.Icon.Nuggets
    ),
    Wraps(
        title = "Wraps",
        icon = Resources.Icon.Wraps
    ),
    Desserts(
        title = "Desserts",
        icon = Resources.Icon.Desserts
    ),
    Fries(
        title = "Fries",
        icon = Resources.Icon.Fries
    ),
    Sauces(
        title = "Sauces",
        icon = Resources.Icon.Sauces
    ),
    Drinks(
        title = "Drinks",
        icon = Resources.Icon.Drinks
    )
}
