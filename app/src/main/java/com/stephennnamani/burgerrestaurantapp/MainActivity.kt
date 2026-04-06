package com.stephennnamani.burgerrestaurantapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.stephennnamani.burgerrestaurantapp.core.data.remote.PayPalWebCheckoutCoordinator
import com.stephennnamani.burgerrestaurantapp.feature.nav.BurgerNavGraph
import com.stephennnamani.burgerrestaurantapp.ui.theme.BurgerRestaurantAppTheme
import org.koin.android.ext.android.inject


class MainActivity : ComponentActivity() {

    private val payPalWebCheckoutCoordinator: PayPalWebCheckoutCoordinator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        )

        payPalWebCheckoutCoordinator.handleReturnIntent(intent)
        setContent {
            BurgerRestaurantAppTheme {
                BurgerNavGraph()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        payPalWebCheckoutCoordinator.handleReturnIntent(intent)
    }
}
