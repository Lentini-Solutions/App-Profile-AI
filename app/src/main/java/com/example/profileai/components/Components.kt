package com.example.profileai.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import com.example.profileai.R
import com.example.profileai.ui.theme.CommonPaddingDefault
import com.example.profileai.ui.theme.CommonPaddingMicro
import com.example.profileai.ui.theme.CommonPaddingMiddle
import com.example.profileai.ui.theme.CommonPaddingNormal
import com.example.profileai.ui.theme.Typography

@Composable
fun ProfileInfoItem(
    stringRes: Int,
    valueStr: String,
    style: TextStyle = Typography.bodyLarge,
    textDecoration: TextDecoration = TextDecoration.None,
    enabled: Boolean,
    iconRes: Int? = null,
    onClick: () -> Unit
){
    Text(text = stringResource(stringRes))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = valueStr,
            modifier = Modifier
                .padding(vertical = CommonPaddingDefault)
                .clickable(
                    onClick = onClick,
                    enabled = enabled
                ),
            style = style,
            textDecoration = textDecoration
        )
        iconRes?.let {
            Icon(
                painterResource(iconRes), contentDescription = null,
                modifier = Modifier.padding(start = CommonPaddingNormal)
            )
        }
    }
}

@Composable
fun SectionTitle(strRes:Int){

        Text(
            text = stringResource(strRes),
            style = Typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(CommonPaddingDefault)
        )

}

@Composable
fun OptionDataUser(strRes: Int, icon: ImageVector, contentDescription: String? = null, onclick: () -> Unit){
    Icon(icon, contentDescription = contentDescription)
    Text(
        text = stringResource(strRes),
        modifier = Modifier
            .padding(CommonPaddingDefault)
            .fillMaxWidth()
            .clickable(onClick = { onclick() }),
        style = Typography.bodyLarge
    )
}