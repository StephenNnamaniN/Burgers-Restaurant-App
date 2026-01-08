package com.stephennnamani.burgerrestaurantapp.feature.component


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.stephennnamani.burgerrestaurantapp.feature.util.Alpha
import com.stephennnamani.burgerrestaurantapp.ui.theme.BrandBrown
import com.stephennnamani.burgerrestaurantapp.ui.theme.BrandYellow
import com.stephennnamani.burgerrestaurantapp.ui.theme.FontSize
import com.stephennnamani.burgerrestaurantapp.ui.theme.Resources
import com.stephennnamani.burgerrestaurantapp.ui.theme.TextWhite
import com.stephennnamani.burgerrestaurantapp.ui.theme.oswaldVariableFont
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainProductCard(
    title: String,
    energyValue: String,
    price: String,
    imageUrl: String,
    paused: Boolean,
    onClick: () -> Unit
){
    val cardHeight = 220.dp
    val brownFraction = 0.5f
    val density = LocalDensity.current.density

    // Animation values
    val imageOffsetX = remember { Animatable(-70f) }
    val imageAlpha= remember { Animatable(0.95f) }
    val imageScale = remember { Animatable(0.98f) }

    LaunchedEffect(paused, imageUrl) {
        if (paused) {
            imageOffsetX.snapTo(-25f)
            imageAlpha.snapTo(1f)
            imageScale.snapTo(1f)
            return@LaunchedEffect
        }
        while (true){
            coroutineScope {
                launch {
                    imageOffsetX.animateTo(
                        targetValue = -25f,
                        animationSpec = tween(900, easing = FastOutSlowInEasing)
                    )
                }
                launch {
                    imageAlpha.animateTo(1f, animationSpec = tween(350))
                }
            }

            imageScale.snapTo(1.05f)
            imageScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )

            delay(5000)

            imageOffsetX.snapTo(90f)
            imageAlpha.snapTo(0.6f)
            imageScale.snapTo(0.98f)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val seamX = maxWidth * brownFraction
            val seamWidth = 26.dp

            // Base background: White section that fills the whole card
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            )

            // Image: Behind the brown section
            AsyncImage(
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "$title product image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(185.dp)
                    .align(Alignment.Center)
                    .offset(x = seamX - 82.dp)
                    .graphicsLayer {
                        translationX = imageOffsetX.value * density
                        alpha = imageAlpha.value
                        scaleX = imageScale.value
                        scaleY = imageScale.value
                    }
            )

            // Brown 50%

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(seamX)
                    .background(BrandBrown)
                    .zIndex(2f)
            ){
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = title,
                        color = TextWhite,
                        fontSize = FontSize.LARGE,
                        fontFamily = oswaldVariableFont(),
                        fontWeight = FontWeight.Bold,
                        lineHeight = 35.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(Resources.Icon.Flame),
                            contentDescription = "Flame icon",
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = energyValue,
                            color = TextWhite.copy(0.65f),
                            fontSize = FontSize.EXTRA_REGULAR,
                            fontFamily = oswaldVariableFont(),
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = price,
                        color = BrandYellow,
                        fontSize = FontSize.REGULAR,
                        fontFamily = oswaldVariableFont(),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            // Soft Gradient
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(seamX)
                    .offset(x = seamX - seamWidth / 2)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                BrandBrown.copy(Alpha.DISABLED),
                                Color.Transparent
                            )
                        )
                    )
                    .zIndex(3f)
            )
        }
    }

}