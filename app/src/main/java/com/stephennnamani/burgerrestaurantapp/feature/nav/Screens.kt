package com.stephennnamani.burgerrestaurantapp.feature.nav

import kotlinx.serialization.Serializable
@Serializable
enum class HomeTab{
    Products,
    Cart,
    Notifications,
    Categories
}


@Serializable
sealed class Screens {
    @Serializable
    data object SplashScreen: Screens()

    @Serializable
    data object AuthScreen: Screens()

    @Serializable
    data class HomeGraph(
        val start: HomeTab = HomeTab.Products
    ): Screens()

    @Serializable
    data object ProductOverviewScreen: Screens()

    @Serializable
    data object Cart: Screens()

    @Serializable
    data object Notifications: Screens()

    @Serializable
    data object Categories: Screens()

    @Serializable
    data object Profile: Screens()

    @Serializable
    data object AdminPanel: Screens()

    @Serializable
    data class ManageProduct(
        val id: String? = null
    ): Screens()

    @Serializable
    data class DetailsScreen(
        val id: String
    ): Screens()

    @Serializable
    data class Checkout(
        val amount: Double
    ): Screens()

    @Serializable
    data class ProductCategoryScreen(
        val category: String
    ): Screens()
}