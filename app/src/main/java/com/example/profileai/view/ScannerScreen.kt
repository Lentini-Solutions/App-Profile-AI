package com.example.profileai.view

import android.widget.Toast
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cursosant.profileaiant.Constants
import com.example.profileai.R
import com.example.profileai.ui.theme.BlackTransparent70
import com.example.profileai.ui.theme.CommonPaddingNano
import com.example.profileai.ui.theme.CommonPaddingNormal
import com.example.profileai.ui.theme.Typography
import com.example.profileai.utils.BarCodeAnalyzer
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.Executors

@Preview(showBackground = true)
@Composable
fun CameraXPreview(){
    ScannerCameraXView(
        modifier = Modifier.padding(top = 24.dp),
        onScanDetected = {_,_ ->},
        onGeoDetected = {},
        onHideCamera = {}
    )
}

@Composable
fun ScannerCameraXView(
    modifier: Modifier,
    onScanDetected: (type: String, value: String) -> Unit,
    onGeoDetected: (Barcode.GeoPoint) -> Unit,
    onHideCamera: () -> Unit
    )
{

    val surfaceRequests = remember { MutableStateFlow<SurfaceRequest?>(null) }
    val surfaceRequest by surfaceRequests.collectAsState(initial = null)
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var detectedBarcode by remember { mutableStateOf<Barcode?>(null) }
    val executor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose {
            executor.shutdown()
        }
    }

    LaunchedEffect(Unit) {
        val cameraProvider = ProcessCameraProvider.awaitInstance(context)
        val preview = androidx.camera.core.Preview.Builder().build().apply {
            setSurfaceProvider { request -> surfaceRequests.value = request }
        }

        /*
        * esto indica al ML que como estrategia, se quede con el último frame analizado,para
        * que en caso de latencia no se quede "pegado"
        * */
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(
                    executor, BarCodeAnalyzer(
                        onBarCodeDetected = { barcode ->
                            detectedBarcode = barcode
                        },
                        onBarCodeFailDetected = { error ->
                            Toast.makeText(
                                context,
                                "Se detecto el error: $error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                )
            }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner = lifecycleOwner,
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
                useCases = arrayOf(preview, imageAnalysis)
            )
        }catch (e: Exception){
            Toast.makeText(context, "Error Camera: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = modifier) {
        surfaceRequest?.let { request ->
            CameraXViewfinder(
                surfaceRequest = request,
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(modifier = Modifier
            .align(Alignment.Center)
            .padding(CommonPaddingNormal)
            .clip(RoundedCornerShape(CommonPaddingNormal))
            .aspectRatio(1f)
            .border(
                width = CommonPaddingNano,
                color = BlackTransparent70,
                shape = RoundedCornerShape(CommonPaddingNormal)
            )
        )

        detectedBarcode?.let {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(CommonPaddingNormal)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BlackTransparent70)
            ) {
                Row(
                    modifier= Modifier.padding(CommonPaddingNormal),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_qr_code_2), contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = it.rawValue.toString(),
                        modifier = Modifier
                            .padding(horizontal = CommonPaddingNormal).weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = Typography.bodyMedium,
                        color = Color.White
                    )
                    Button(
                        onClick = {
                            when(it.valueType){
                                Barcode.TYPE_EMAIL -> it.email?.address.let {
                                    email -> onScanDetected(Constants.P_EMAIL, email ?: "")
                                }
                                Barcode.TYPE_URL -> it.url?.url.let {
                                        url -> onScanDetected(Constants.P_WEBSITE, url ?: "")
                                }
                                Barcode.TYPE_PHONE -> it.phone?.number.let {
                                        phone -> onScanDetected(Constants.P_PHONE, phone ?: "")
                                }
                                Barcode.TYPE_GEO -> it.geoPoint?.let {geoPoint ->
                                    onGeoDetected(geoPoint)
                                }
                                else -> { onScanDetected(Constants.P_NAME, it.rawValue ?: "") }
                            }
                            onHideCamera()
                            detectedBarcode = null
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.scanner_add_button),
                            modifier = Modifier.padding(end = CommonPaddingNormal)
                        )
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
            }
        }
    }
}