package com.example.profileai.view

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.cursosant.profileaiant.Constants
import com.example.profileai.R
import com.example.profileai.data.source.userFake
import com.example.profileai.domain.model.User
import com.example.profileai.ui.theme.CommonPaddingDefault
import com.example.profileai.ui.theme.CommonPaddingNormal
import com.example.profileai.ui.theme.ProfileAITheme
import com.example.profileai.ui.theme.imageSizeLarge
import com.example.profileai.view_model.MainViewModel

@Preview(showSystemUi = true)
@Composable
fun EditScreenPreview(){
    ProfileAITheme {
        EditContentView(
            initUser = userFake,
            isCameraActive = false,
            onSaveUser = {},
            onBack = {},
            onActiveCameraX = {}
        )
    }
}

@Composable
fun EditView(vm: MainViewModel, onBack: () -> Unit){
    val user by vm.user.collectAsState()
    var activeCameraX by remember { mutableStateOf(false) }

    EditContentView(
        initUser = user,
        isCameraActive = activeCameraX,
        onSaveUser = { user ->
            vm.saveUser(user)
            onBack()
        },
        onBack = onBack,
        onActiveCameraX = { active ->
            activeCameraX = active
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditContentView(
    initUser: User,
    isCameraActive: Boolean,
    onSaveUser: (user: User) -> Unit,
    onBack: () -> Unit,
    onActiveCameraX: (Boolean) -> Unit,
) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf("") }

    /*
    * Esto es para que una vez se componga la vista, las mutableStateOf se pueblen
    * con la información que nos traemos -- por ahora -- del DataStore
    * entonces, cada vez que se vuelve a abrir para editar, se cargan los datos desde el ds,
    * además, evitamos que al rotar pantalla (ejemplo) se reseteen los datos
    * */
    LaunchedEffect(initUser) {
        name = initUser.name
        email = initUser.email
        website = initUser.website
        phone = initUser.phone
        imageUri = initUser.image
        latitude = initUser.latitude
        longitude = initUser.longitude
    }

    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = {uri ->
            uri?.let {
                val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(
                    it,
                    flags
                )
                imageUri = it.toString()
            }
        }
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.edit_title))
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            painterResource(R.drawable.ic_arrow_back_ios),
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        onSaveUser(
                            User(
                                id = initUser.id,
                                name = name,
                                email= email,
                                website = website,
                                phone = phone,
                                image = imageUri,
                                latitude = latitude,
                                longitude = longitude
                            )
                        )
                    }) {
                        Icon(
                            painterResource(R.drawable.ic_check),
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = { onActiveCameraX(!isCameraActive) }) {
                        Icon(
                            imageVector = if(!isCameraActive)
                                Icons.Filled.AutoAwesome else
                                    Icons.Default.Stop,
                            contentDescription = null
                        )
                    }
                }
            )
        },
    ) { innerPadding ->

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(CommonPaddingDefault)
            .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(CommonPaddingNormal)
        ) {

            EditImageScannerSection(
                imageUri = imageUri,
                onImageClick = {
                    galleryLauncher.launch(arrayOf("image/*"))
                },
                onScanDetected = { type, value ->
                    when(type){
                        Constants.P_NAME -> name = value
                        Constants.P_WEBSITE -> website = value
                        Constants.P_PHONE -> phone = value
                        Constants.P_EMAIL -> email = value
                    }
                },
                onGeoDetected = { lat, long ->
                    latitude = lat
                    longitude = long
                },
                isCameraActive = isCameraActive
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = {
                    Text(text = stringResource(R.string.hint_name))
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(text = stringResource(R.string.hint_email))
                },
                modifier = Modifier.fillMaxWidth().padding(top = CommonPaddingNormal),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email
                )
            )

            OutlinedTextField(
                value = website,
                onValueChange = { website = it },
                label = {
                    Text(text = stringResource(R.string.hint_website))
                },
                modifier = Modifier.fillMaxWidth().padding(top = CommonPaddingNormal),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Uri
                )
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = {
                    Text(text = stringResource(R.string.hint_phone))
                },
                modifier = Modifier.fillMaxWidth().padding(top = CommonPaddingNormal),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                )
            )

            OutlinedTextField(
                value = latitude,
                onValueChange = { latitude = it },
                label = {
                    Text(text = stringResource(R.string.hint_latitude))
                },
                modifier = Modifier.fillMaxWidth().padding(top = CommonPaddingNormal),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                )
            )

            OutlinedTextField(
                value = longitude,
                onValueChange = { longitude = it },
                label = {
                    Text(text = stringResource(R.string.hint_longitude))
                },
                modifier = Modifier.fillMaxWidth().padding(top = CommonPaddingNormal),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                )
            )
        }
    }
}

@Composable
fun EditImageScannerSection(
    imageUri: String,
    onImageClick: () -> Unit,
    onScanDetected: (type: String, value: String) -> Unit,
    onGeoDetected: (lat: String, long: String) -> Unit,
    isCameraActive: Boolean,
){
    Column() {
        AnimatedVisibility(visible = !isCameraActive) {
            Box() {
                AsyncImage(
                    model = if(imageUri.isEmpty()) R.drawable.img_cursos_ant else imageUri.toUri(),
                    contentDescription = null,
                    modifier = Modifier.size(imageSizeLarge)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                FilledTonalButton(
                    onClick = { onImageClick() },
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Icon(painter = painterResource(R.drawable.ic_image_search), contentDescription = null)
                }
            }
        }

        AnimatedVisibility(visible = isCameraActive) {
            ScannerCameraXView(
                modifier = Modifier.fillMaxSize().background(Color.LightGray).height(imageSizeLarge),
                onScanDetected = onScanDetected,
                onGeoDetected = { onGeoDetected(it.lat.toString(), it.lng.toString()) },
                onHideCamera = { isCameraActive }
            )
        }
    }
}