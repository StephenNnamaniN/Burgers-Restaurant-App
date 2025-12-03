package com.stephennnamani.burgerrestaurantapp.feature.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.stephennnamani.burgerrestaurantapp.ui.theme.BorderIdle
import com.stephennnamani.burgerrestaurantapp.ui.theme.FontSize
import com.stephennnamani.burgerrestaurantapp.ui.theme.SurfaceLight
import com.stephennnamani.burgerrestaurantapp.ui.theme.TextPrimary

@Composable
fun BurgerSelectTextField(
    modifier: Modifier = Modifier,
    text: String,
    iconUrl: String? = null,
    onClick: () -> Unit,
    placeholder: String = "",
    isError: Boolean = false
){
    val contentColor = if (text.isNotBlank()) TextPrimary else TextPrimary.copy(0.6f)
    Row (
        modifier
            .background(SurfaceLight)
            .border(
                width = 1.dp,
                color = BorderIdle,
                shape = RoundedCornerShape(6.dp)
            )
            .clip(RoundedCornerShape(6.dp))
            .clickable {onClick()}
            .padding(
                vertical = 16.dp,
                horizontal = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ){
       if (iconUrl != null) {
           AsyncImage(
               model = iconUrl,
               contentDescription = "Country flag",
               modifier = Modifier
                   .size(24.dp)
                   .clip(CircleShape),
               contentScale = ContentScale.Crop
           )
           Spacer(modifier = Modifier.width(14.dp))
           Text(
               text = text.ifBlank { placeholder },
               fontSize = FontSize.REGULAR,
               color = contentColor,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis
           )
       } else {
           Text(
               text = text.ifBlank { placeholder },
               fontSize = FontSize.REGULAR,
               color = contentColor,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis
           )
       }
    }
    if (isError){
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Invalid selection",
            color = Color.Red.copy(0.9f),
            fontSize = FontSize.SMALL
        )
    }
}