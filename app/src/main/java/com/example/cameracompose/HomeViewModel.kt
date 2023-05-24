package com.example.cameracompose

import android.Manifest
import android.os.Build
import android.os.Environment
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class HomeViewModel : ViewModel() {
    fun getImagesFromGallery(): StateFlow<List<File>> {
        val imagesStateFlow = MutableStateFlow<List<File>>(emptyList())
        val imageFolder = File(Environment.getExternalStorageDirectory(), "Pictures/CameraX-Folder")
        val imageFiles = imageFolder.listFiles()?.filter {
            it.extension == CameraViewModel.image_extension
        }?.toList() ?: emptyList()

        imagesStateFlow.value = imageFiles
        return imagesStateFlow
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
