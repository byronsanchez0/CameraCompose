package com.example.cameracompose.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    var icon: ImageVector, var route: String
) {

    object Home : BottomNavItem(
        Icons.Outlined.Home,
        "home"
    )

    object OpenCamera : BottomNavItem(
        Icons.Outlined.CameraAlt,
        "openCamera"
    )


}