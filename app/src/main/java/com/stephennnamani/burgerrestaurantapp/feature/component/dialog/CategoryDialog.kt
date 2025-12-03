package com.stephennnamani.burgerrestaurantapp.feature.component.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stephennnamani.burgerrestaurantapp.core.data.models.ProductCategory
import com.stephennnamani.burgerrestaurantapp.feature.util.Alpha
import com.stephennnamani.burgerrestaurantapp.ui.theme.BrandBrown
import com.stephennnamani.burgerrestaurantapp.ui.theme.FontSize
import com.stephennnamani.burgerrestaurantapp.ui.theme.Gray
import com.stephennnamani.burgerrestaurantapp.ui.theme.IconWhite
import com.stephennnamani.burgerrestaurantapp.ui.theme.Resources
import com.stephennnamani.burgerrestaurantapp.ui.theme.SurfaceLight
import com.stephennnamani.burgerrestaurantapp.ui.theme.TextBrand
import com.stephennnamani.burgerrestaurantapp.ui.theme.TextPrimary
import com.stephennnamani.burgerrestaurantapp.ui.theme.TextWhite

@Composable
fun CategoryDialog(
    categories: List<ProductCategory>,
    onDismiss: () -> Unit,
    onSelectedCategory: (ProductCategory) -> Unit
){
    var selectedCategory by remember { mutableStateOf<ProductCategory?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Select a category",
                fontSize = FontSize.EXTRA_MEDIUM,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .verticalScroll(rememberScrollState())
            ) {
               categories.forEach { currentCategory ->
                   Card(
                       modifier = Modifier
                           .fillMaxWidth()
                           .clickable {selectedCategory = currentCategory}
                           .padding(
                               top = 4.dp,
                               bottom = 4.dp
                           ),
                       elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                       colors = CardDefaults.cardColors(
                           containerColor = if (currentCategory == selectedCategory) BrandBrown
                           else Gray
                       )
                   ) {
                       Row(
                           modifier = Modifier
                               .fillMaxWidth()
                               .padding(12.dp),
                           verticalAlignment = Alignment.CenterVertically,
                           horizontalArrangement = Arrangement.SpaceBetween
                       ) {
                           Icon(
                               painter = painterResource(id = currentCategory.icon),
                               contentDescription = currentCategory.title,
                               modifier = Modifier.size(24.dp),
                               tint = Color.Unspecified
                           )
                           Spacer(modifier = Modifier.width(12.dp))
                           Text(
                               modifier = Modifier.weight(1f),
                               text = currentCategory.title,
                               color = if (currentCategory == selectedCategory) TextWhite
                                       else TextPrimary,
                               fontSize = FontSize.REGULAR
                           )
                           Spacer(modifier = Modifier.width(12.dp))
                           AnimatedVisibility(
                               visible = selectedCategory == currentCategory
                           ) {
                               Icon(
                                   painter = painterResource(Resources.Icon.Checkmark),
                                   contentDescription = "Checkmark icon",
                                   modifier = Modifier.size(14.dp),
                                   tint = IconWhite
                               )
                           }
                       }
                   }
               }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedCategory?.let { onSelectedCategory(it)}
                },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = TextBrand
                )
            ) {
                Text(
                    text = "Confirm",
                    fontSize = FontSize.REGULAR,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = TextPrimary.copy(alpha = Alpha.HALF)
                )
            ) {
                Text(
                    text = "Cancel",
                    fontSize = FontSize.REGULAR,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        containerColor = SurfaceLight
    )
}