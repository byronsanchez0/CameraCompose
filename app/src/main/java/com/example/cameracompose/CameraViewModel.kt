package com.example.cameracompose

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.media.ExifInterface
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.Path

class CameraViewModel : ViewModel() {
    val visiblePermissionsDialogQueue = mutableStateListOf<String>()
    var imageCapture: ImageCapture? = null
    private val _capturedPhoto = MutableStateFlow<ByteArray?>(null)
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var cameraExecutor: ExecutorService

    fun dismissDialog() {
        visiblePermissionsDialogQueue.removeFirst()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if (!isGranted && !visiblePermissionsDialogQueue.contains(permission)) {
            visiblePermissionsDialogQueue.add(permission)
        }
    }




    @RequiresApi(Build.VERSION_CODES.O)

    fun getLocationFromImage(imagePath: String): Pair<Double, Double>? {
        try {
            val exifInterface = ExifInterface(imagePath)
            val latLong = FloatArray(2)
            val hasLatLong = exifInterface.getLatLong(latLong)

            if (hasLatLong) {
                val latitude = latLong[0].toDouble()
                val longitude = latLong[1].toDouble()
                return Pair(latitude, longitude)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    @SuppressLint("MissingPermission")
    fun takePhotoWithLocation(context: Context) {
//        allPermissionsGranted(context)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        val imageCapture = imageCapture ?: return
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, IMG_TYPE)
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Folder")
            }
        }

        viewModelScope.launch {

            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                val metadata = ImageCapture.Metadata().apply {
                    this.location = location

                }
                val outputOptions = ImageCapture.OutputFileOptions.Builder(
                    context.contentResolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                ).setMetadata(metadata).build()

                imageCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onError(exc: ImageCaptureException) {
                            Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                        }

                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            val msg = "Photo capture succeeded: ${output.savedUri}"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            Log.d(TAG, msg)
                        }
                    },
                )
            }

        }

    }

//    fun takePhoto(context: Context) {
//        // Get a stable reference of the modifiable image capture use case
//        val imageCapture = imageCapture ?: return
//
//        // Create time stamped name and MediaStore entry.
//        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
//            .format(System.currentTimeMillis())
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
//            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
//            }
//        }
//
//        // Create output options object which contains file + metadata
//
//        val outputOptions = ImageCapture.OutputFileOptions
//            .Builder(
//                context.contentResolver,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                contentValues
//            )
//            .build()
//
//        // Set up image capture listener, which is triggered after photo has
//        // been taken
//        imageCapture.takePicture(
//            outputOptions,
//            ContextCompat.getMainExecutor(context),
//            object : ImageCapture.OnImageSavedCallback {
//                override fun onError(exc: ImageCaptureException) {
//                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
//                }
//
//                override fun
//                        onImageSaved(output: ImageCapture.OutputFileResults) {
//                    val msg = "Photo capture succeeded: ${output.savedUri}"
//                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
//                    Log.d(TAG, msg)
//                }
//            }
//        )
//
//    }
// fun allPermissionsGranted(context: Context) = REQUIRED_PERMISSIONS.all {
//    val code = ContextCompat.checkSelfPermission(context, it)
//    return code == PackageManager.PERMISSION_GRANTED
//}
//     fun onRequestPermissionsResult(
//        requestCode: Int, permissions: Array<String>, grantResults:
//        IntArray,
//        context: Context
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CODE_PERMISSIONS) {
//            if (allPermissionsGranted(context)) {
//                startCamera()
//            } else {
//                Toast.makeText(
//                    requireContext(),
//                    "Permissions not granted by the user.",
//                    Toast.LENGTH_SHORT
//                ).show()
//
//            }
//        }
//    }



    companion object {
        const val IMG_TYPE = "image/jpeg"
        const val IMG_DIRECTORY = "Pictures/CameraX-Folder"
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
                    add(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }.toTypedArray()

        const val image_extension: String = "jpg"
    }
}