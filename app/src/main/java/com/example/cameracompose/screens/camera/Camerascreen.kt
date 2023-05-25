package com.example.cameracompose.screens.camera

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.cameracompose.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi


@RequiresApi(Build.VERSION_CODES.O)
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





