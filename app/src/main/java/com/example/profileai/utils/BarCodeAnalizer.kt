package com.example.profileai.utils

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class BarCodeAnalyzer(
    private val onBarCodeDetected: (Barcode) -> Unit,
    private val onBarCodeFailDetected: (String) -> Unit,
): ImageAnalysis.Analyzer {

    //con esta configuración optimizamos para facilitar el reconocimiento de códigos
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_CODABAR,
            Barcode.FORMAT_DATA_MATRIX,
            Barcode.FORMAT_AZTEC
        )
        .build()
    private val scanner = BarcodeScanning.getClient(options)

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val image = imageProxy.image
        image?.let {
            //con esto, ML Kit SIEMPRE va a ver la imagen derecha
            val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    barcodes.firstOrNull()?.let { barcode ->
                        onBarCodeDetected(barcode)
                    }
                }
                .addOnFailureListener { error ->
                    onBarCodeFailDetected(error.message.toString())
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}