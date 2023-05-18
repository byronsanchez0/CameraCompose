package com.example.cameracompose.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    var title: String, var icon: ImageVector, var route: String
) {

    object Home : BottomNavItem(
        "Home",
        Icons.Outlined.Home,
        "home"
    )
    object OpenCamera : BottomNavItem(
        "OpenCamera",
        Icons.Outlined.Home,
        "openCamera"
    )



}