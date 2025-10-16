package com.stephennnamani.burgerrestaurantapp.feature.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.stephennnamani.burgerrestaurantapp.ui.theme.FontSize
import com.stephennnamani.burgerrestaurantapp.ui.theme.IconPrimary
import com.stephennnamani.burgerrestaurantapp.ui.theme.Resources
import com.stephennnamani.burgerrestaurantapp.ui.theme.SurfaceLight
import com.stephennnamani.burgerrestaurantapp.ui.theme.TextPrimary

@Composable
fun ProfilePhotoEditor(
    photoUrl: String?,
    isUploading: Boolean,
    progress: Float,
    onPickClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .size(132.dp)
            .clip(CircleShape)
            .background(SurfaceLight)
            .clickable(enabled = !isUploading) {onPickClick()},
        contentAlignment = Alignment.Center
    ){
        if (photoUrl.isNullOrBlank()){
            Icon(
                painter = painterResource(Resources.Icon.Person),
                contentDescription = "User Profile icon",
                tint = IconPrimary,
                modifier = Modifier.size(64.dp)
            )
        } else {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "User profile picture",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(Resources.Icon.Person),
                error = painterResource(Resources.Icon.Close)
            )
        }

        //Upload Overlay
        if (isUploading){
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(0.45f)),
                contentAlignment = Alignment.Center
            ){
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        progress = { progress.coerceIn(0f, 1f)},
                        color = ProgressIndicatorDefaults.circularColor,
                        strokeWidth = ProgressIndicatorDefaults.CircularStrokeWidth,
                        trackColor = ProgressIndicatorDefaults.circularColor,
                        strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        color = Color.White,
                        fontSize = FontSize.SMALL
                    )
                }
            }
        }

    }
    // Change picture hint button
    Box(
        modifier = Modifier
            .offset(y = 4.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        TextButton(
            onClick = onPickClick,
            enabled = !isUploading
        ) {
            Icon(
                painter = painterResource(Resources.Icon.Camera),
                contentDescription = "Camera icon",
                tint = IconPrimary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Change Photo",
                color = TextPrimary,
                fontSize = FontSize.REGULAR
            )
        }
    }
}