package com.stephennnamani.burgerrestaurantapp.feature.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.stephennnamani.burgerrestaurantapp.core.data.models.Product
import com.stephennnamani.burgerrestaurantapp.ui.theme.BorderIdle
import com.stephennnamani.burgerrestaurantapp.ui.theme.FontSize
import com.stephennnamani.burgerrestaurantapp.ui.theme.Resources
import com.stephennnamani.burgerrestaurantapp.ui.theme.SurfaceLight
import com.stephennnamani.burgerrestaurantapp.ui.theme.TextPrimary
import com.stephennnamani.burgerrestaurantapp.ui.theme.TextSecondary
import com.stephennnamani.burgerrestaurantapp.ui.theme.oswaldVariableFont

@Composable
fun CartProductCard(
    modifier: Modifier = Modifier,
    product: Product,
    onClick: (String) -> Unit,
    trailingContent: (@Composable RowScope.() -> Unit)? = null
){
    Row(
        modifier = modifier.fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = BorderIdle,
                shape = RoundedCornerShape(12.dp)
            )
            .background(SurfaceLight)
            .clickable {onClick(product.id)}
    ) {
        AsyncImage(
            modifier = Modifier
                .width(80.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = BorderIdle,
                    shape = RoundedCornerShape(12.dp)
                ),
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(product.productImage)
                .crossfade(enable = true)
                .build(),
            contentDescription = "Product image",
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = product.title,
                fontSize = FontSize.MEDIUM,
                color = TextPrimary,
                fontFamily = oswaldVariableFont(),
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "£${product.price}",
                    fontSize = FontSize.EXTRA_REGULAR,
                    color = TextSecondary,
                    fontFamily = oswaldVariableFont(),
                    fontWeight = FontWeight.Bold,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier.size(14.dp),
                        painter = painterResource(Resources.Icon.Flame),
                        contentDescription = "Flame icon",
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${product.energyValue}kcal",
                        fontSize = FontSize.EXTRA_SMALL,
                        color = TextPrimary,
                        fontFamily = oswaldVariableFont()
                    )
                }
            }
            if (trailingContent != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(end = 4.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.End,
                    content = trailingContent
                )
            }
        }
    }

}