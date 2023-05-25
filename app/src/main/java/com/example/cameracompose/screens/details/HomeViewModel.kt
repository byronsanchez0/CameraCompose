package com.example.cameracompose.screens.details

import android.os.Environment
import androidx.lifecycle.ViewModel
import com.example.cameracompose.screens.camera.CameraViewModel
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
}
