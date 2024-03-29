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
import androidx.compose.runtime.LaunchedEffect
import com.example.cameracompose.dialogs.PermissionDialog
import com.example.cameracompose.navigation.MainScreen
import com.example.cameracompose.screens.camera.CameraViewModel
import com.example.cameracompose.textprovider.CameraPermissionText
import com.example.cameracompose.textprovider.LocationPermissionText
import com.example.cameracompose.ui.theme.CameraComposeTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val permissionsToRequest = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )
    private val viewModel = CameraViewModel()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val dialogQueue = viewModel.visiblePermissionsDialogQueue
            val multiplePermissionResult = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions(),
                onResult = { perms ->
                    permissionsToRequest.forEach { permission ->
                        viewModel.onPermissionResult(
                            permission = permission, isGranted = perms[permission] == true
                        )
                    }
                }
            )
            dialogQueue.reversed().forEach { permission ->
                PermissionDialog(permissionTextProvider = when (permission) {
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
                    onGoSetting = { openSetting() }
                )
            }
            LaunchedEffect(Unit) {
                multiplePermissionResult.launch(
                    permissionsToRequest
                )
            }
            CameraComposeTheme {
                MainScreen()
            }
        }

    }

    private fun Activity.openSetting() {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        ).also(::startActivity)
    }
}


