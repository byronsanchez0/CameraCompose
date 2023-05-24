package com.example.cameracompose

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.cameracompose.dialogs.PermissionDialog
import com.example.cameracompose.textprovider.CameraPermissionText
import com.example.cameracompose.textprovider.LocationPermissionText
import com.example.cameracompose.ui.theme.CameraComposeTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val permissionsToRequest = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )
    private val viewModel = CameraViewModel()
    @OptIn(ExperimentalPermissionsApi::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val dialogQueue = viewModel.visiblePermissionsDialogQueue
            val multiplePermissionResult = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions(),
                onResult = {perms->
                    permissionsToRequest.forEach { permission ->
                        viewModel.onPermissionResult(
                            permission = permission, isGranted = perms[permission] == true
                        )
                    }
                }
            )
            dialogQueue.reversed().forEach{permission ->
                PermissionDialog(permissionTextProvider = when(permission) {
                    Manifest.permission.CAMERA -> {
                        CameraPermissionText()
                    }

                    Manifest.permission.ACCESS_FINE_LOCATION -> {
                        LocationPermissionText()
                    }
                    else -> return@forEach
                }, isPermanentlyDeclined = !shouldShowRequestPermissionRationale(permission),
                    onDismiss = viewModel::dismissDialog,
                    onAccept = {
                        viewModel.dismissDialog()
                        multiplePermissionResult.launch(
                            arrayOf(permission)
                        )
                    },
                    onGoSetting = {openSetting()}
                    )
            }
            LaunchedEffect(Unit){
                multiplePermissionResult.launch(
                    permissionsToRequest
                )
            }
            CameraComposeTheme {
                // A surface container using the 'background' color from the theme
                MainScreen()
            }
//            @Composable
//            fun RequestPermission() {
//                var permissionsState = rememberMultiplePermissionsState(
//                    permissions = listOf(
//                        Manifest.permission.CAMERA,
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.ACCESS_COARSE_LOCATION,
//                        Manifest.permission.READ_EXTERNAL_STORAGE,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE
//                    )
//                )
//
//                permissionsState.permissions.forEach { perms ->
//                    when (perms.permission) {
//                        Manifest.permission.CAMERA -> {
//                            CameraPermissionText()
//                        }
//
//                        Manifest.permission.ACCESS_FINE_LOCATION -> {
//                            LocationPermissionText()
//                        }
//                    }
//                }
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = Modifier.size(300.dp)
//                ) {
//                    Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
//                        Text(text = "Allow permission")
//                    }
//                }
//            }
        }

    }
    private fun Activity.openSetting() {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        ).also(::startActivity)
    }
}


