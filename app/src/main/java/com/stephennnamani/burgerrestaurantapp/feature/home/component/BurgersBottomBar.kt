package com.stephennnamani.burgerrestaurantapp.feature.home.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.stephennnamani.burgerrestaurantapp.feature.home.domain.BottomBarDestinations
import com.stephennnamani.burgerrestaurantapp.ui.theme.IconPrimary
import com.stephennnamani.burgerrestaurantapp.ui.theme.IconSecondary
import com.stephennnamani.burgerrestaurantapp.ui.theme.SurfaceDark

@Composable
fun BurgersBottomBar(
    modifier: Modifier = Modifier,
    selected: BottomBarDestinations,
    onSelect: (BottomBarDestinations) -> Unit
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .padding(
                vertical = 12.dp,
                horizontal = 24.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BottomBarDestinations.entries.forEach { destinations ->
            val animatedTint by animateColorAsState(
                targetValue = if (selected == destinations) IconSecondary else IconPrimary
            )
            IconButton(
                onClick = { onSelect(destinations) }
            ) {
                Icon(
                    painter = painterResource(destinations.icon),
                    contentDescription = "Bottom bar destination icon",
                    tint = animatedTint
                )
            }
        }
    }
}
