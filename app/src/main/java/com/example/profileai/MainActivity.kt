package com.example.profileai

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cursosant.profileaiant.Constants
import com.example.profileai.ui.theme.ProfileAITheme
import com.example.profileai.view.MainView
import com.example.profileai.view.ProfileView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProfileAITheme {
                val cameraPermission = rememberPermissionState(
                    Manifest.permission.CAMERA
                )
                MainView(hasPermission = cameraPermission.status.isGranted)
            }
        }

        if(!checkPermissionGranted()){
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        var permissionRequest = ArrayList<String>()
        for (permission in RUNTIME_PERMISSION){
            if(!isPermissionGranted(permission)){
                permissionRequest.add(permission)
            }
        }

        if(permissionRequest.isNotEmpty()){
            ActivityCompat.requestPermissions(
                this,
                permissionRequest.toTypedArray(),
                Constants.PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun checkPermissionGranted(): Boolean {
        for (permission in RUNTIME_PERMISSION){
            if(!isPermissionGranted(permission)){
                return false
            }
        }
        return true
    }

    private fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val RUNTIME_PERMISSION = arrayOf(Manifest.permission.CAMERA)
    }
}