package com.example.cameracompose.screens.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
fun DetailScreen(
    path: String,
    detailsViewModel: DetailsViewModel
) {

    val location = detailsViewModel.getLocationFromImage(path)
    val photo = LatLng(location?.first?: 0.0, location?.second?:0.0 )
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(photo, 10f)
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(path)
                .build(),
            contentDescription = "icon",
            contentScale = ContentScale.Inside,
            modifier = Modifier.size(width = 500.dp, height = 300.dp),
        )
        GoogleMap(
            modifier = Modifier
                .size(width = 500.dp, height = 500.dp),
            cameraPositionState = cameraPositionState,
        ) {
            Marker(
                state = MarkerState(position = photo),
                title = "photo",
                snippet = "this photo"
            )
        }
    }


}

