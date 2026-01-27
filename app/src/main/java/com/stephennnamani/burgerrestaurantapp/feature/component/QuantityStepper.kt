package com.stephennnamani.burgerrestaurantapp.feature.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stephennnamani.burgerrestaurantapp.ui.theme.BrandYellow
import com.stephennnamani.burgerrestaurantapp.ui.theme.FontSize

@Composable
fun QuantityStepper(
    quantity: Int,
    onMinusClick: () -> Unit,
    onPlusClick: () -> Unit
){
    Row(verticalAlignment = Alignment.CenterVertically) {
        StepperButton(text = "-", onClick = onMinusClick)
        Text(
            text = quantity.toString().padStart(2, '0'),
            fontSize = FontSize.REGULAR,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp)

        )
        StepperButton(text = "+", onClick = onPlusClick)
    }
}

@Composable
private fun StepperButton(
    text: String,
    onClick: () -> Unit
){
    OutlinedButton(
        onClick = onClick,
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.size(24.dp),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, BrandYellow)
    ) {
        Text(
            text = text,
            fontSize = FontSize.REGULAR,
            fontWeight = FontWeight.Bold
        )
    }
}