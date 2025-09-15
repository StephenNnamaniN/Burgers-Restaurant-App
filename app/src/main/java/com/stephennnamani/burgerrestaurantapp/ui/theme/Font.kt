package com.stephennnamani.burgerrestaurantapp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.stephennnamani.burgerrestaurantapp.R

@Composable
fun oswaldVariableFont() = FontFamily(
    Font(R.font.oswald_variable_font_wght)
)

@Composable
fun sentientVariable() = FontFamily(
    Font(R.font.sentient_variable)
)

object FontSize {
    val EXTRA_SMALL = 10.sp
    val SMALL = 12.sp
    val REGULAR = 14.sp
    val EXTRA_REGULAR =16.sp
    val MEDIUM = 18.sp
    val EXTRA_MEDIUM = 20.sp
    val LARGE = 30.sp
    val EXTRA_LARGE = 40.sp
}