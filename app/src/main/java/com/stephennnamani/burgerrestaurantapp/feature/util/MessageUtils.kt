package com.stephennnamani.burgerrestaurantapp.feature.util

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import java.time.Duration

object MessageUtils {
    @Composable
    fun ShowToast(message: String, duration: Int = Toast.LENGTH_SHORT){
        val context = LocalContext.current
        LaunchedEffect(message) {
            if (message.isNotEmpty()){
                Toast.makeText(context, message, duration).show()
            }
        }
    }
}