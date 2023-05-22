package com.example.cameracompose

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.location.Location
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
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
    var imageCapture: ImageCapture? = null

    private val _capturedPhoto = MutableStateFlow<ByteArray?>(null)
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var cameraExecutor: ExecutorService



    @RequiresApi(Build.VERSION_CODES.O)
    fun getImagesFromGallery(): StateFlow<List<File>> {
        val imagesStateFlow = MutableStateFlow<List<File>>(emptyList())
        val imageFolder = File(Environment.getExternalStorageDirectory(), IMG_DIRECTORY)
        val imageFiles = imageFolder.listFiles()?.filter {
            it.extension == image_extension
        }?.toList() ?: emptyList()

        imagesStateFlow.value = imageFiles
        return imagesStateFlow
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getFileMetadata(filePath: String): BasicFileAttributes? {
        try {
            val file = Path(filePath)
            return Files.readAttributes(file, BasicFileAttributes::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }


    //    private val context = LocalContext.current
//
//
//
//    private fun startCamera() {
//
//
//
//        val cameraProviderFuture = ProcessCameraProvider.getInstance()
//
//        cameraProviderFuture.addListener({
//            // Used to bind the lifecycle of cameras to the lifecycle owner
//            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
//
//            // Preview
//            val preview = Preview.Builder()
//                .build()
//                .also {
//                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
//                }
//
//            imageCapture = ImageCapture.Builder().build()
//
//            // Select back camera as a default
//            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//
//            try {
//                // Unbind use cases before rebinding
//                cameraProvider.unbindAll()
//
//                // Bind use cases to camera
//                cameraProvider.bindToLifecycle(
//                    this, cameraSelector, preview, imageCapture
//                )
//
//            } catch (exc: Exception) {
//                Log.e(TAG, "Use case binding failed", exc)
//            }
//
//        }, ContextCompat.getMainExecutor(requireContext()))
//    }
    @SuppressLint("MissingPermission")
    fun takePhotoWithLocation(context: Context) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        val imageCapture = imageCapture ?: return
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, IMG_TYPE)
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, IMG_DIRECTORY)
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

    fun takePhoto(context: Context) {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            }
        )

    }

    companion object {
        const val IMG_TYPE = "image/jpeg"
        const val IMG_DIRECTORY = "Pictures/CameraX-Folder"

        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
                    add(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }.toTypedArray()

        const val camera_folder: String = "Pictures/CameraX-Image"
        const val image_extension: String = "jpg"
    }
}