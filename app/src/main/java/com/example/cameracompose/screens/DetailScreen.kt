package com.example.cameracompose.screens

import android.media.ExifInterface
import android.media.MediaMetadataRetriever
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.cameracompose.DetailsViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.first

@Composable
fun DetailScreen(
    path: String,
    detailsViewModel: DetailsViewModel
) {

    val location = detailsViewModel.getLocationFromImage(path)
    val photo = LatLng(location?.first?: 0.0, location?.second?:0.0 )
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
            modifier = Modifier.size(width = 300.dp, height = 300.dp)
        )
        GoogleMap(
            modifier = Modifier
//                .fillMaxSize()
                .size(width = 300.dp, height = 300.dp),
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

