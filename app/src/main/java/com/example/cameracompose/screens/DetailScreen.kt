package com.example.cameracompose.screens

import android.media.ExifInterface
import android.media.MediaMetadataRetriever
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun DetailScreen(path: String) {
    val location = getLocationFromImage(path)
    val photo = LatLng(location?.first?: 0.0, location?.second?:0.0)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(photo, 10f)
    }

    Column() {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(path)
                .build(),
            contentDescription = "icon",
            contentScale = ContentScale.Inside,
        )
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(position = photo),
                title = "photo",
                snippet = "this photo"
            )
        }
    }



}

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