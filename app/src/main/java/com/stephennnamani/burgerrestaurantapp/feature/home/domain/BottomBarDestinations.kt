package com.stephennnamani.burgerrestaurantapp.feature.home.domain

import com.stephennnamani.burgerrestaurantapp.feature.nav.Screens
import com.stephennnamani.burgerrestaurantapp.ui.theme.Resources

enum class BottomBarDestinations(
    val icon: Int,
    val title: String,
    val screen: Screens
) {
    ProductOverviewScreen(
        icon = Resources.Icon.Home,
        title = "Burgers",
        screen = Screens.ProductOverviewScreen
    ),
    CartScreen(
    icon = Resources.Icon.ShoppingCart,
    title = "Cart",
    screen = Screens.Cart
    ),
    NotificationsScreen(
    icon = Resources.Icon.Bell,
    title = "Notifications",
    screen = Screens.Notifications
    ),
    CategoriesScreen(
    icon = Resources.Icon.Categories,
    title = "Categories",
    screen = Screens.Categories
    )
}