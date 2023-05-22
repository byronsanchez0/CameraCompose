package com.example.cameracompose.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.cameracompose.CameraViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(viewmodel: CameraViewModel, navHostController: NavHostController) {
    var myList = viewmodel.getImagesFromGallery().collectAsState(emptyList())
    var images = myList.value.reversed()

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(200.dp),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        content = {
            items(images.size) { photo ->
                val image = images[photo]

                AsyncImage(
                    model = image,
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clickable {
                            navHostController.navigate("DetailScreen?path=${image.path}")
                        }
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    )
//        LazyColumn(
//            Modifier
//                .padding(it)
//                .fillMaxSize()
//        ) {
//            items(myList){
//                Image(
//                    painter = rememberAsyncImagePainter(myList.size),
//                    contentDescription = stringResource(R.string.album_descr),
//                    modifier = Modifier
//                        .size(65.dp)
//                        .clip(RoundedCornerShape(4.dp))
//                )
//            }
//        }


}
