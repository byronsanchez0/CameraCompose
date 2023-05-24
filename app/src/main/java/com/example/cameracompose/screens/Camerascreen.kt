package com.example.cameracompose.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.ImageButton
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.sharp.AddCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.cameracompose.CameraViewModel
import com.example.cameracompose.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(viewModel: CameraViewModel) {
    val imageCapture = remember {
        mutableStateOf<ImageCapture?>(null)
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }
    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }


        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.takePhotoWithLocation(context) },
                    modifier = Modifier.padding(vertical = 100.dp)
                ) {
                    Icon(

                        painter = painterResource(id = R.drawable.baseline_motion_photos_on_24),
                        contentDescription = null,
                        modifier = Modifier
                            .size(45.dp)
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.Center
        ) {

            AndroidView(
                factory = { previewView },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = androidx.camera.core.Preview.Builder()
                        .build()
                        .also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                    imageCapture.value = ImageCapture.Builder().build()
                    viewModel.imageCapture = imageCapture.value
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture.value
                        )
                    } catch (exce: Exception) {
                        Log.e("Exc", "CameraX ${exce.localizedMessage}")
                    }
                }, ContextCompat.getMainExecutor(context))


            }

        }





}


@ExperimentalPermissionsApi
@Composable
private fun FeatureThatRequiresCameraPermission() {
    val context = LocalContext

    // Camera permission state
    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )
    val readFilesPermissionState = rememberPermissionState(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    val writeFilesPermissionState = rememberPermissionState(
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    val fineLocationPermissionStates = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
//                && writeFilesPermissionState.shouldShowRationale
    )
    val coarseLocationPermissionStates = rememberPermissionState(
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    if (cameraPermissionState.hasPermission
//        && readFilesPermissionState.hasPermission
//        && writeFilesPermissionState.hasPermission
        && fineLocationPermissionStates.hasPermission
        && coarseLocationPermissionStates.hasPermission
    ) {
        Text("Permissions Granted")
    } else {
        Column {
            val textToShow = if (
                cameraPermissionState.shouldShowRationale
//                && readFilesPermissionState.shouldShowRationale
                && fineLocationPermissionStates.shouldShowRationale
                && coarseLocationPermissionStates.shouldShowRationale
            ) {
                // If the user has denied the permission but the rationale can be shown,
                // then gently explain why the app requires this permission
                "The camera, access to Files and Location is important for this app. Please grant the permission."
//                        cameraPermissionState.launchPermissionRequest()
            } else {
                // If it's the first time the user lands on this feature, or the user
                // doesn't want to be asked again for this permission, explain that the
                // permission is required
                "Camera permission required for this feature to be available. " +
                        "Please grant the permission" +
                        AlertDialogSample()

            }
            Text(textToShow)
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Request permission")
            }
            Button(onClick = { fineLocationPermissionStates.launchPermissionRequest() }) {
                Text("Request permission")
            }
            Button(onClick = { coarseLocationPermissionStates.launchPermissionRequest() }) {
                Text("Request permission")
            }

        }
    }

    fun permissionsState() {

    }
}



