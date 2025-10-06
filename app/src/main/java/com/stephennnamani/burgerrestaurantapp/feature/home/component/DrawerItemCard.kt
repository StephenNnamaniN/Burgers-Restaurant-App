package com.stephennnamani.burgerrestaurantapp.feature.home.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.stephennnamani.burgerrestaurantapp.feature.home.domain.DrawerItem
import com.stephennnamani.burgerrestaurantapp.ui.theme.FontSize
import com.stephennnamani.burgerrestaurantapp.ui.theme.IconSecondary
import com.stephennnamani.burgerrestaurantapp.ui.theme.TextWhite

@Composable
fun DrawerItemCard(
    drawerItem: DrawerItem,
    onClick: () -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(size = 99.dp))
            .clickable {onClick()}
            .padding(
                vertical = 12.dp,
                horizontal = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(drawerItem.icon),
            contentDescription = "Drawer item icon",
            tint = IconSecondary
        )
        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = drawerItem.title,
            color = TextWhite,
            fontSize = FontSize.EXTRA_REGULAR
        )
    }
}