package com.stephennnamani.burgerrestaurantapp.feature.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stephennnamani.burgerrestaurantapp.ui.theme.BrandBrown
import com.stephennnamani.burgerrestaurantapp.ui.theme.FontSize
import com.stephennnamani.burgerrestaurantapp.ui.theme.TextWhite
import com.stephennnamani.burgerrestaurantapp.ui.theme.oswaldVariableFont
import kotlinx.coroutines.delay

@Composable
fun CategoryChip(
    title: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.72f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Box(
        modifier = Modifier
            .width(140.dp)
            .height(90.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = Alignment.TopCenter
    ){
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(12.dp),
            color = BrandBrown,
            tonalElevation = 6.dp,
            shadowElevation = 6.dp,
            onClick = {
                pressed = true
                onClick()
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    color = TextWhite,
                    fontSize = FontSize.EXTRA_MEDIUM,
                    fontFamily = oswaldVariableFont(),
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        Image(
            painter = painterResource(iconRes),
            contentDescription = title,
            modifier = Modifier
                .size(70.dp)
                .offset(y = (-30).dp),
            contentScale = ContentScale.Fit
        )
    }
    LaunchedEffect(pressed) {
        if (pressed) {
            delay(120)
            pressed = false
        }
    }
}