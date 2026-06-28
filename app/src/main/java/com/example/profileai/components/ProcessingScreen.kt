package com.example.profileai.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.profileai.ui.theme.ProgressBackground

@Composable
fun ProcessingView(){
        Box(Modifier
            .fillMaxSize()
            .background(ProgressBackground)
            .clickable(interactionSource = null, indication = null){},
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
}