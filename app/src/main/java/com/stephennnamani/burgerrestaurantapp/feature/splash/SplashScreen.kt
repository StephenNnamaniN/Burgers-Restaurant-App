package com.stephennnamani.burgerrestaurantapp.feature.splash

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.stephennnamani.burgerrestaurantapp.R
import com.stephennnamani.burgerrestaurantapp.ui.theme.BrandBrown
import com.stephennnamani.burgerrestaurantapp.ui.theme.BrandYellow
import com.stephennnamani.burgerrestaurantapp.ui.theme.FontSize
import com.stephennnamani.burgerrestaurantapp.ui.theme.TextWhite
import com.stephennnamani.burgerrestaurantapp.ui.theme.oswaldVariableFont
import com.stephennnamani.burgerrestaurantapp.ui.theme.sentientVariable
import org.koin.compose.koinInject

@Composable
fun SplashScreen(
    navigateToAuth: () -> Unit,
){
    val scale = remember { Animatable(0f) }

    LaunchedEffect(key1 = true, block = {
        scale.animateTo(targetValue = 0.7f, animationSpec = tween(
            durationMillis = 600, easing = {
                OvershootInterpolator(7f)
                    .getInterpolation(it)
        }
        ))
    })


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(1.dp)
            .background(BrandYellow),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.burgers),
            contentDescription = "Logo image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(
                    width = 440.dp,
                    height = 440.dp
                )
                .padding(10.dp)
                .scale(scale.value)
        )
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = stringResource(R.string.splash_header),
            fontFamily = oswaldVariableFont(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.splash_subtext),
            fontFamily = sentientVariable(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(100.dp))
        SplashButton(
            onClick = {
                navigateToAuth()
            }
        )
    }
}

@Composable
fun SplashButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color = BrandBrown,
    onClick: () -> Unit
){
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
            .clickable{onClick()},
        shape = RoundedCornerShape(99.dp),
        color = backgroundColor
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.splash_btn_text),
                color = TextWhite,
                fontFamily = sentientVariable(),
                fontSize = FontSize.EXTRA_REGULAR
            )
            Spacer(modifier = Modifier.width(24.dp))
            Icon(
                painter = painterResource(R.drawable.log_in),
                contentDescription = "Login icon",
                tint = Color.White
            )
        }
    }
}
