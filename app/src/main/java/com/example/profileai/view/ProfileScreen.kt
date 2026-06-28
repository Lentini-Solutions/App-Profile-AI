package com.example.profileai.view

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.cursosant.profileaiant.Constants
import com.example.profileai.R
import com.example.profileai.components.ProfileInfoItem
import com.example.profileai.data.source.userFake
import com.example.profileai.domain.model.User
import com.example.profileai.ui.theme.CommonPaddingDefault
import com.example.profileai.ui.theme.Typography
import com.example.profileai.ui.theme.imageSizeLarge
import com.example.profileai.ui.theme.imageSizeMedium
import com.example.profileai.ui.theme.imageSizeSmall
import com.example.profileai.view_model.MainViewModel

@Preview(showSystemUi = true)
@Composable
fun ProfilePreview(){
    ProfileContent(userFake, areClicksEnable = true, imgSizeKey = "", onEdit = {}, onSettings = {})
}

@Composable
fun ProfileView(vm: MainViewModel,onEdit: () -> Unit, onSettings: () -> Unit) {
    val user by vm.user.collectAsState()
    val imgSize by vm.sizeImage.collectAsState()
    val areClicksEnable by vm.areClicksEnable.collectAsState()
    ProfileContent(
        user,
        areClicksEnable = areClicksEnable,
        imgSizeKey = imgSize,
        onEdit = {
            onEdit()
        },
        onSettings = { onSettings() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    user: User,
    imgSizeKey: String,
    areClicksEnable: Boolean,
    onEdit: () -> Unit, onSettings: () -> Unit
) {

    val imgSize = when(imgSizeKey){
        Constants.DS_SIZE_SMALL -> imageSizeSmall
        Constants.DS_SIZE_MEDIUM -> imageSizeMedium
        else -> imageSizeLarge
    }

    val context = LocalContext.current
    val launch = { intent: Intent ->
        try {
            context.startActivity(intent)
        }catch (e: Exception){
            Toast.makeText(context, R.string.error_apps_no_resolve, Toast.LENGTH_SHORT).show()
        }
    }
    val packageName = context.packageName

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.app_name))
                },
                actions = {
                    IconButton(onClick = { onEdit() }) {
                        Icon(painterResource(R.drawable.ic_edit),contentDescription = null)
                    }
                    IconButton(onClick = { onSettings() }) {
                        Icon(painterResource(R.drawable.ic_settings),contentDescription = null)
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AsyncImage(
                model = if(user.image.isEmpty()) R.drawable.img_cursos_ant else user.image.toUri(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(bottom = CommonPaddingDefault)
                    .size(imgSize)
                    .clip(CircleShape)
            )

            ProfileInfoItem(
                stringRes = R.string.hint_name,
                valueStr = user.name,
                enabled = areClicksEnable,
                style = Typography.titleLarge,
                onClick = {
                    val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                        putExtra(SearchManager.QUERY, user.name)
                    }
                    launch(intent)
                }
            )

            ProfileInfoItem(
                stringRes = R.string.hint_email,
                valueStr = user.email,
                enabled = areClicksEnable,
                textDecoration = TextDecoration.Underline,
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data= "mailto:".toUri()
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(user.email))
                    }
                    launch(intent)
                }
            )

            ProfileInfoItem(
                stringRes = R.string.hint_website,
                valueStr = user.website,
                enabled = areClicksEnable,
                textDecoration = TextDecoration.Underline,
                onClick = {
                    val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                        putExtra(SearchManager.QUERY, user.website.toUri())
                    }
                    startIntent(context, intent, R.string.string_title_chooser, R.string.error_apps_no_resolve)
                }
            )

            ProfileInfoItem(
                stringRes = R.string.hint_phone,
                valueStr = user.phone,
                enabled = areClicksEnable,
                style = Typography.headlineSmall,
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = "tel:${user.phone}".toUri()
                    }
                    launch(intent)
                }
            )

            HorizontalDivider(modifier = Modifier.weight(1f).padding(top = CommonPaddingDefault))
            ProfileInfoItem(
                stringRes = R.string.hint_location,
                valueStr = stringResource(R.string.profile_show_map),
                enabled = areClicksEnable,
                iconRes = R.drawable.ic_map,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = "geo:0,0?q=${user.latitude},${user.longitude}".toUri()
                    }
                    launch(intent)
                }
            )
            ProfileInfoItem(
                stringRes = R.string.profile_settings,
                valueStr = stringResource(R.string.settings_title),
                iconRes = R.drawable.ic_settings,
                enabled = areClicksEnable,
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", packageName, null)
                    }
                    launch(intent)
                }
            )
        }
    }
}

private fun startIntent(context: Context, intent: Intent, msg: Int, errorMsg: Int){
    val chooser: Intent = Intent.createChooser(intent,
        context.getString(msg))
    if(chooser.resolveActivity(context.packageManager) != null){
        context.startActivity(chooser)
    }else{
        Toast.makeText(context,
            context.getString(errorMsg),
            Toast.LENGTH_SHORT)
            .show()
    }

}