package com.stephennnamani.burgerrestaurantapp.feature.nav

import kotlinx.serialization.Serializable


@Serializable
sealed class Screens {
    @Serializable
    data object SplashScreen: Screens()

    @Serializable
    data object AuthScreen: Screens()

    @Serializable
    data object HomeGraph: Screens()

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
}