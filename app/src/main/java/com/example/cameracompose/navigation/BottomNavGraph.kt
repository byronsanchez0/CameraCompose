package com.example.cameracompose.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cameracompose.CameraViewModel
import com.example.cameracompose.DetailsViewModel
import com.example.cameracompose.HomeViewModel
import com.example.cameracompose.navigation.BottomNavItem
import com.example.cameracompose.screens.CameraScreen
import com.example.cameracompose.screens.DetailScreen
import com.example.cameracompose.screens.HomeScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomNavGraph(navHostController: NavHostController) {
    val viewmodel = remember {
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
            CameraScreen(viewmodel = viewmodel)
        }
        composable("DetailScreen?path={path}", ){ backStackEntry ->
            println(backStackEntry)
            DetailScreen(backStackEntry.arguments?.getString("path")?:"",detailViewModel)
        }
    }
}