package com.example.profileai.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.profileai.R
import com.example.profileai.ui.theme.BlueDark700
import com.example.profileai.ui.theme.SplashScreenBackground
import com.example.profileai.ui.theme.Typography

@Preview(showSystemUi = true)
@Composable
fun SplashScreenPreview(){
    SplashScreen()
}

@Composable
fun SplashScreen(){

    val infiniteTransition = rememberInfiniteTransition(label = "Splash ANT")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1_000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Icon Scale ANT"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashScreenBackground),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(painterResource(
            R.drawable.ic_launcher_foreground), contentDescription = null,
            tint = BlueDark700,
            modifier = Modifier.size(128.dp)
                .scale(scale)

        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.app_name),
            style = Typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))
        CircularProgressIndicator()
    }

}