package com.example.profileai.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cursosant.profileaiant.Constants
import com.example.profileai.R
import com.example.profileai.components.OptionDataUser
import com.example.profileai.components.SectionTitle
import com.example.profileai.ui.theme.CommonPaddingDefault
import com.example.profileai.ui.theme.CommonPaddingNormal
import com.example.profileai.ui.theme.ProfileAITheme
import com.example.profileai.ui.theme.Typography
import com.example.profileai.view_model.MainViewModel

@Preview(showSystemUi = true)
@Composable
fun SettingsPreview() {
    ProfileAITheme {
        SettingsContentView(
            imgSize = "",
            areClicksEnabled = true,
            onCheckedChange = {},
            onBack = {},
            onRestoreSettings = {},
            onRestoreAll = {},
            onClearUserData = {},
            onImageSizeChange = {}
        )
    }
}

@Composable
fun SettingsView(vm: MainViewModel, onBack: () -> Unit){

    val areClicksEnabled by vm.areClicksEnable.collectAsState()
    val imgSize by vm.sizeImage.collectAsState()

    SettingsContentView(
        areClicksEnabled = areClicksEnabled,
        imgSize = imgSize,
        onBack = { onBack() },
        onCheckedChange = {
            vm.saveEnableClicks(it)
        },
        onImageSizeChange = {
            vm.saveSizeImage(it)
        },
        onClearUserData = {
            vm.clearData()
        },
        onRestoreSettings = {
            vm.saveEnableClicks(true)
            vm.saveSizeImage(Constants.DS_SIZE_LARGE)
        },
        onRestoreAll = {
            vm.clearData()
            vm.saveEnableClicks(true)
            vm.saveSizeImage(Constants.DS_SIZE_LARGE)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContentView(
    areClicksEnabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    imgSize: String,
    onBack: () -> Unit,
    onClearUserData: () -> Unit,
    onRestoreSettings: () -> Unit,
    onRestoreAll: () -> Unit,
    onImageSizeChange: (String) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.settings_title))
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            painterResource(R.drawable.ic_arrow_back_ios),
                            contentDescription = null)
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally) {
                SettingsTouchSection(areClicksEnabled, onCheckedChange = onCheckedChange)
            HorizontalDivider(modifier = Modifier
                .padding(top = CommonPaddingNormal)
                .padding(CommonPaddingDefault)
            )
            SettingsUiSection(
                imgSize = imgSize,
                onImageSizeChange = onImageSizeChange
            )

            HorizontalDivider(modifier = Modifier
                .padding(top = CommonPaddingNormal)
                .padding(CommonPaddingDefault)
            )
            SettingsDataSection(
                onClearUserData = { onClearUserData() },
                onRestoreSettings = { onRestoreSettings() },
                onRestoreAll = { onRestoreAll() }
            )
        }
    }
}

@Composable
fun SettingsTouchSection(areClicksEnabled: Boolean, onCheckedChange: (Boolean) -> Unit){

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.settings_enable_clicks),
            style = Typography.titleMedium,
            modifier = Modifier
                .weight(1f)
                .padding(CommonPaddingDefault)
        )
        Switch(
            checked = areClicksEnabled,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(CommonPaddingDefault),
            thumbContent = if (areClicksEnabled) {
                {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize),
                    )
                }
            } else {
                {
                    Icon(
                        imageVector = Icons.Filled.Block,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize),
                    )
                }
            }
        )
        Text(
            text = stringResource(R.string.settings_touch_title),
            style = Typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = CommonPaddingNormal)
        )
    }

}

@Composable
fun SettingsUiSection(imgSize: String, onImageSizeChange: (String) -> Unit){

    var showDialog by remember { mutableStateOf(false)}
    val sizeLabel = when(imgSize){
        Constants.DS_SIZE_SMALL -> stringResource(R.string.settings_img_size_small)
        Constants.DS_SIZE_MEDIUM -> stringResource(R.string.settings_img_size_medium)
        else -> stringResource(R.string.settings_img_size_large)
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        SectionTitle(R.string.settings_ui_title)
        Spacer(Modifier.width(200.dp))
        ListItem(
            headlineContent = { stringResource(R.string.settings_ui_img_size) },
            supportingContent = { Text(text = sizeLabel) },
            modifier = Modifier
                .clickable { showDialog = true }
                .fillMaxWidth()
        )

        if(showDialog){
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text(text = stringResource(R.string.settings_ui_img_size))
                },
                text = {
                    Column() {
                        val options = listOf(
                            Constants.DS_SIZE_SMALL to stringResource(R.string.settings_img_size_small),
                            Constants.DS_SIZE_MEDIUM to stringResource(R.string.settings_img_size_medium),
                            Constants.DS_SIZE_LARGE to stringResource(R.string.settings_img_size_large),
                        )
                        options.forEach { (key, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable{
                                        onImageSizeChange(key)
                                        showDialog = false
                                    }
                                    .padding(CommonPaddingDefault),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = imgSize == key, onClick = null)
                                Spacer(modifier = Modifier.width(CommonPaddingNormal))
                                Text(text = label)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text(text = stringResource(R.string.dialog_ok))
                    }
                }
            )
        }
    }

}

@Composable
fun SettingsDataSection(
    onClearUserData: () -> Unit,
    onRestoreSettings: () -> Unit,
    onRestoreAll: () -> Unit
){

    SectionTitle(R.string.settings_data_title)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(CommonPaddingDefault)
    ) {
        OptionDataUser(
            strRes = R.string.settings_delete_data,
            icon = Icons.Default.PersonOff,
            onclick = { onClearUserData() }
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(CommonPaddingDefault)
    ) {
        OptionDataUser(
            strRes = R.string.settings_restore_settings,
            icon = Icons.Default.Restore,
            onclick = { onRestoreSettings() }
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(CommonPaddingDefault)
    ) {
        OptionDataUser(
            strRes = R.string.settings_restore_all,
            icon = Icons.Default.RestoreFromTrash,
            onclick = { onRestoreAll() }
        )
    }
}