package com.stephennnamani.burgerrestaurantapp.feature.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stephennnamani.burgerrestaurantapp.feature.auth.AuthScreen
import com.stephennnamani.burgerrestaurantapp.feature.splash.SplashScreen

@Composable
fun BurgerNavGraph(startDestination: Screens = Screens.SplashScreen){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ){
        composable<Screens.SplashScreen> {
            SplashScreen(
                navigateToAuth = {
                    navController.navigate(Screens.AuthScreen){
                        popUpTo<Screens.SplashScreen> { inclusive = true }
                    }
                }
            )
        }

        composable<Screens.AuthScreen> {
            AuthScreen()
        }
    }
}