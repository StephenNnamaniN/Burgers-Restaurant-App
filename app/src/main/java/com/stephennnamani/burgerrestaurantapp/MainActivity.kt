package com.stephennnamani.burgerrestaurantapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.stephennnamani.burgerrestaurantapp.feature.nav.BurgerNavGraph
import com.stephennnamani.burgerrestaurantapp.ui.theme.BurgerRestaurantAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BurgerRestaurantAppTheme {
                BurgerNavGraph()
            }
        }
    }
}
