package com.stephennnamani.burgerrestaurantapp.feature.component.dialog

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.stephennnamani.burgerrestaurantapp.core.data.models.Country
import com.stephennnamani.burgerrestaurantapp.feature.component.BurgerTextField
import com.stephennnamani.burgerrestaurantapp.feature.component.ErrorCard
import com.stephennnamani.burgerrestaurantapp.feature.util.Alpha
import com.stephennnamani.burgerrestaurantapp.ui.theme.FontSize
import com.stephennnamani.burgerrestaurantapp.ui.theme.IconWhite
import com.stephennnamani.burgerrestaurantapp.ui.theme.Resources
import com.stephennnamani.burgerrestaurantapp.ui.theme.SurfaceBrand
import com.stephennnamani.burgerrestaurantapp.ui.theme.SurfaceDark
import com.stephennnamani.burgerrestaurantapp.ui.theme.SurfaceLight
import com.stephennnamani.burgerrestaurantapp.ui.theme.TextBrand
import com.stephennnamani.burgerrestaurantapp.ui.theme.TextPrimary

@Composable
fun CountryPickerDialog(
    countries: List<Country>,
    selectedCountry: Country?,
    onDismiss: () -> Unit,
    onConfirmClick: (Country) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var localSelection by remember(selectedCountry) { mutableStateOf(selectedCountry) }

    val filteredCountries = remember(searchQuery, countries) {
        val query = searchQuery.trim().lowercase()
        if (query.isEmpty()) countries
        else countries.filter { country ->
            country.name.lowercase().contains(query) ||
                    "+${country.dialCode}".contains(query) ||
                    country.code.lowercase().contains(query)
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Select a country",
                fontSize = FontSize.EXTRA_MEDIUM,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .height(360.dp)
                    .fillMaxWidth()
            ) {
                BurgerTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it},
                    placeholder = "Dial code or country"
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (filteredCountries.isNotEmpty()){
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(
                            items = filteredCountries,
                            key = { it.code }
                        ){ country ->
                            CountryPicker(
                                country = country,
                                isSelected = localSelection?.code == country.code,
                                onSelect = { localSelection = country }
                            )
                        }
                    }
                } else {
                    ErrorCard(
                        modifier = Modifier.weight(1f),
                        message = "Dial code not found"
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { localSelection?.let(onConfirmClick)},
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

@Composable
private fun CountryPicker(
    modifier: Modifier = Modifier,
    country: Country,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val saturation = remember { Animatable(if(isSelected) 1f else 0f) }

    LaunchedEffect(isSelected) {
        saturation.animateTo(if (isSelected) 1f else 0f)
    }
     val colorMatrix = remember(saturation.value) {
         ColorMatrix().apply {
             setToSaturation(saturation.value)
         }
     }

    Row(
        modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape),
            model = country.flagUrl,
            contentDescription = "${country.name} flag",
            colorFilter = ColorFilter.colorMatrix(colorMatrix)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = "+${country.dialCode} (${country.name})",
            fontSize = FontSize.REGULAR,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Selector(isSelected = isSelected)
    }
}

@Composable
private fun Selector(
    modifier: Modifier = Modifier,
    isSelected: Boolean
) {
    val animatedBackground by animateColorAsState(
        targetValue = if (isSelected) SurfaceBrand else SurfaceDark
    )
    Box(
        modifier = modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(animatedBackground),
        contentAlignment = Alignment.Center
    ){
        AnimatedVisibility(
            visible = isSelected
        ) {
           Icon(
                modifier = modifier.size(14.dp),
                painter = painterResource(Resources.Icon.Checkmark),
                contentDescription = "Checkmark icon",
                tint = IconWhite
            )
        }
    }
}
