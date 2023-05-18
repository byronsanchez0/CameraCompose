package com.example.cameracompose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cameracompose.navigation.BottomNavItem
import com.example.cameracompose.screens.CameraScreen
import com.example.cameracompose.screens.HomeScreen

@Composable
fun BottomNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route
    ) {
        composable(route= BottomNavItem.Home.route){
            HomeScreen()
        }
        composable(route= BottomNavItem.OpenCamera.route){
            CameraScreen()
        }
    }
}