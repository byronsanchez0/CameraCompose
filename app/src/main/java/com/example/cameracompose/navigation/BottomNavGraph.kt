package com.example.cameracompose.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cameracompose.screens.camera.CameraViewModel
import com.example.cameracompose.screens.details.DetailsViewModel
import com.example.cameracompose.screens.details.HomeViewModel
import com.example.cameracompose.screens.camera.CameraScreen
import com.example.cameracompose.screens.details.DetailScreen
import com.example.cameracompose.screens.home.HomeScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomNavGraph(navHostController: NavHostController) {
    val cameraViewModel = remember {
        CameraViewModel()
    }
    val homeViewModel = remember {
        HomeViewModel()
    }
    val detailViewModel = remember {
        DetailsViewModel()
    }
    NavHost(
        navController = navHostController,
        startDestination = BottomNavItem.Home.route
    ) {
        composable(route= BottomNavItem.Home.route){
            HomeScreen(homeViewModel, navHostController)
        }
        composable(route= BottomNavItem.OpenCamera.route){
            CameraScreen(cameraViewModel)
        }
        composable("DetailScreen?path={path}", ){ backStackEntry ->
            println(backStackEntry)
            DetailScreen(backStackEntry.arguments?.getString("path")?:"",detailViewModel)
        }
    }
}