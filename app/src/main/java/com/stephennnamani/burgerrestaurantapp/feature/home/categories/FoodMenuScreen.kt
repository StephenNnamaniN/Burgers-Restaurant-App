package com.stephennnamani.burgerrestaurantapp.feature.home.categories

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.stephennnamani.burgerrestaurantapp.core.data.models.ProductCategory
import com.stephennnamani.burgerrestaurantapp.feature.util.Alpha
import com.stephennnamani.burgerrestaurantapp.ui.theme.BorderIdle
import com.stephennnamani.burgerrestaurantapp.ui.theme.FontSize
import com.stephennnamani.burgerrestaurantapp.ui.theme.SurfaceLight
import com.stephennnamani.burgerrestaurantapp.ui.theme.TextPrimary
import com.stephennnamani.burgerrestaurantapp.ui.theme.oswaldVariableFont

@Composable
fun FoodMenuScreen(
    onCategoryClick: (ProductCategory) -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Discover Our Menu",
            fontFamily = oswaldVariableFont(),
            fontSize = FontSize.MEDIUM,
            color = TextPrimary.copy(Alpha.SIXTY_PERCENT)
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(ProductCategory.entries) { category ->
                FoodMenuCategoryCard(
                    category = category,
                    onClick = { onCategoryClick(category)}
                )
            }
        }
    }
}

@Composable
private fun FoodMenuCategoryCard(
    category: ProductCategory,
    onClick: () -> Unit
){
    Box(
        modifier = Modifier
            .width(140.dp)
            .height(140.dp)
            .padding(top = 12.dp)
            .clickable (onClick = onClick),
        contentAlignment = Alignment.TopCenter
    ){
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .height(110.dp)
                .align(Alignment.BottomCenter)
                .offset(y = 30.dp)
                .border(
                    1.dp,
                    BorderIdle,
                    RoundedCornerShape(12.dp)
                ),
            shape = RoundedCornerShape(12.dp),
            color = SurfaceLight,
            tonalElevation = 6.dp,
            shadowElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = category.title,
                    fontFamily = oswaldVariableFont(),
                    fontSize = FontSize.MEDIUM,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
        Surface(
            modifier = Modifier
                .width(100.dp)
                .height(100.dp)
                .clip(RoundedCornerShape(50.dp))
                .border(
                    1.dp,
                    BorderIdle,
                    RoundedCornerShape(50.dp)
                )
        ) {
            Icon(
                painter = painterResource(category.icon),
                contentDescription = category.title,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(14.dp)
                    .align(Alignment.Center)
            )
        }
    }
}