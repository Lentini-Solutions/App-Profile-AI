package com.example.profileai.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.compose.rememberNavController
import com.example.profileai.R
import com.example.profileai.navigation.NavGraph
import com.example.profileai.ui.theme.CommonPaddingDefault
import com.example.profileai.ui.theme.CommonPaddingLarge
import com.example.profileai.ui.theme.Typography
import com.example.profileai.view_model.MainViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainView(vm: MainViewModel = koinViewModel(), hasPermission: Boolean){
    val navController = rememberNavController()
    NavGraph(navController, vm = vm)
    if(!hasPermission){
        Text(
            text = stringResource(R.string.permission_msg_warning),
            modifier = Modifier
                .padding(CommonPaddingDefault)
                .padding(top = CommonPaddingLarge),
            textAlign = TextAlign.Center,
            style = Typography.labelLarge.copy(
                shadow = Shadow(
                    color = Color.Red,
                    offset = Offset(0f, 0f),
                    blurRadius = 12f
                )
            )

        )

    }
}