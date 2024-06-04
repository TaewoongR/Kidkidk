package com.example.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.AdvancedMarkerOptions
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.text.SimpleDateFormat
import java.util.Calendar

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: SharedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val range = uiState.signalRange
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    var uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = true)) }
    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }

    val koreaAerospaceUniv = LatLng(37.6005, 126.8660)

    // 초기 카메라 위치
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(koreaAerospaceUniv, 16.5f)
    }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .padding(top = screenWidth * 0.04f)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "메뉴",
                tint = Color.Black,
                modifier = Modifier
                    .size(50.dp)
                    .padding(start = 10.dp)
                    .clickable {
                    }
            )
            Text(
                text = "지도",
                style = TextStyle(fontSize = 23.sp, fontWeight = FontWeight.Bold)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "메뉴",
                tint = Color.Black,
                modifier = Modifier
                    .size(50.dp)
                    .padding(end = 10.dp)
                    .clickable {
                    }
            )
        }
        HorizontalDivider(
            modifier = Modifier.width(screenWidth * 0.9f).padding(top = screenWidth * 0.01f, bottom = screenWidth * 0.1f),
            color = Color.LightGray.copy(alpha = 0.8f), thickness = 2.dp)
        Box(
            Modifier
                .width(screenWidth * 0.9f)
                .height(screenWidth * 0.9f),
            contentAlignment = Alignment.Center
        ) {
            GoogleMap(
                modifier = Modifier.matchParentSize(),
                properties = properties,
                uiSettings = uiSettings,
                cameraPositionState = cameraPositionState // 카메라 위치 상태 추가
            ) {
                MapMarker(
                    context = LocalContext.current,
                    position = LatLng(37.5998, 126.8655),
                    title = "Sensor1",
                    iconResourceId = if (range == 0) R.drawable.warning_green else if (range > 0 && range <= 5 * 1000) R.drawable.warning_yellow else R.drawable.warning_red,
                    cameraPositionState = cameraPositionState
                )
            }
        }

        Box(modifier = Modifier.offset(y = screenWidth * 0.05f).align(Alignment.Start).padding(start = screenWidth * 0.05f, bottom = 10.dp)){
            Text("한국항공대학교", style = TextStyle(fontSize = 23.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black))
        }
        Box(modifier = Modifier.offset(y = screenWidth * 0.05f).align(Alignment.Start).padding(start = screenWidth * 0.05f, bottom = 10.dp)){
            Text("경기 고양시 덕양구 항공대학로 76", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Normal, color = Color.Gray))
        }
        Box(modifier = Modifier.offset(y = screenWidth * 0.05f).align(Alignment.Start).padding(start = screenWidth * 0.05f, bottom = 10.dp)){
            Text("불법 주정차 : ${isParked()}", style = TextStyle(fontSize = 23.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black))
        }
        if(isParked() == "O") {
            Box(
                modifier = Modifier.offset(y = screenWidth * 0.05f).align(Alignment.Start)
                    .padding(start = screenWidth * 0.05f, bottom = 30.dp)
            ) {
                Text(
                    "${whenParked()}",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray
                    )
                )
            }
        }else{
            Box(
                modifier = Modifier.offset(y = screenWidth * 0.05f).align(Alignment.Start)
                    .padding(start = screenWidth * 0.05f, bottom = 30.dp)
            ) {
                Text(
                    "",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray
                    )
                )
            }
        }

        Box(modifier = Modifier.offset(y = screenWidth * 0.05f)){
            setButton(screenWidth, "신고하기", viewModel, uiState, navController)
        }
    }
}

fun isParked(): String{
    return "O"
}

fun whenParked(): String{
    val date = System.currentTimeMillis()
    val dateString = Calendar.getInstance()
    dateString.setTimeInMillis(date)
    return SimpleDateFormat("yyyy/MM/dd hh:mm").format(dateString.time)
}

@Composable
fun MapMarker(
    context: Context,
    position: LatLng,
    title: String,
    @DrawableRes iconResourceId: Int,
    cameraPositionState: CameraPositionState
) {
    var icon by remember { mutableStateOf<BitmapDescriptor?>(null) }

    LaunchedEffect(cameraPositionState.isMoving) {
        snapshotFlow { cameraPositionState.position.zoom }
            .collect { zoom ->
                val scaledIcon = bitmapDescriptionFromVector(
                    context, iconResourceId, zoom
                )
                icon = scaledIcon
            }
    }

    if (icon != null) {
        Marker(
            state = MarkerState(position = position),
            title = title,
            icon = icon
        )
    }
}

fun bitmapDescriptionFromVector(
    context: Context,
    vectorResId: Int,
    zoom: Float
): BitmapDescriptor? {
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null

    // Scale the drawable size based on the zoom level
    val scaleFactor = zoom / 16.5f / 13f // Adjust the divisor based on your initial zoom level
    val width = (drawable.intrinsicWidth * scaleFactor * 0.8).toInt()
    val height = (drawable.intrinsicHeight * scaleFactor * 0.8).toInt()
    drawable.setBounds(0, 0, width, height)

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

@Composable
fun setButton(totalWidth: Dp, text: String, viewModel: SharedViewModel, uiState: SharedUiState, navController: NavController){
    Box(
        modifier = Modifier
            .width(totalWidth * 0.9f)
            .aspectRatio(1f / 0.16f)
            .background(Color.Black, shape = RoundedCornerShape(12.dp))
            .clickable {
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, style = TextStyle(fontSize = 23.sp, fontWeight = FontWeight.Bold, color = Color.White))
    }
}