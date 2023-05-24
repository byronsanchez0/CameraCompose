package com.example.cameracompose

import android.media.ExifInterface
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DetailsViewModel : ViewModel() {

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
}

