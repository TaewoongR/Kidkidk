package com.example.ui

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar

@Composable
fun ReportScreen(
    navController: NavController,
    viewModel: SharedViewModel
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

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .fillMaxSize(),
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
                            navController.navigate("home_route") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                )
                Text(
                    text = "신고",
                    style = TextStyle(fontSize = 23.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(end = screenWidth * 0.45f)
                )
            }
            HorizontalDivider(
                modifier = Modifier
                    .width(screenWidth * 0.9f)
                    .padding(
                        top = screenWidth * 0.01f,
                        bottom = screenWidth * 0.1f
                    ),
                color = Color.LightGray.copy(alpha = 0.8f),
                thickness = 2.dp
            )
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
                        range = range,
                        cameraPositionState = cameraPositionState
                    )
                }
            }

            Box(
                modifier = Modifier
                    .offset(y = screenWidth * 0.05f)
                    .align(Alignment.Start)
                    .padding(start = screenWidth * 0.05f, bottom = 10.dp)
            ) {
                Text(
                    "한국항공대학교",
                    style = TextStyle(fontSize = 23.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                )
            }
            Box(
                modifier = Modifier
                    .offset(y = screenWidth * 0.05f)
                    .align(Alignment.Start)
                    .padding(start = screenWidth * 0.05f, bottom = 10.dp)
            ) {
                Text(
                    "경기 고양시 덕양구 항공대학로 76",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Normal, color = Color.Gray)
                )
            }
            Box(
                modifier = Modifier
                    .offset(y = screenWidth * 0.05f)
                    .align(Alignment.Start)
                    .padding(start = screenWidth * 0.05f, bottom = 10.dp)
            ) {
                Text(
                    "불법 주정차 : ${isParked(uiState.signalRange)}",
                    style = TextStyle(fontSize = 23.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                )
            }
            if (uiState.signalRange != 0) {
                Box(
                    modifier = Modifier
                        .offset(y = screenWidth * 0.05f)
                        .align(Alignment.Start)
                        .padding(start = screenWidth * 0.05f, bottom = 30.dp)
                ) {
                    Text(
                        "${uiState.parkedStart} - ${uiState.signalRange}초 경과",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray
                        )
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .offset(y = screenWidth * 0.05f)
                        .align(Alignment.Start)
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

            Box(modifier = Modifier.offset(y = screenWidth * 0.05f)) {
                reportButton(screenWidth, "신고하기", snackbarHostState, viewModel, uiState)
            }
        }
    }
}

@Composable
fun reportButton(totalWidth: Dp, text: String, snackbarHostState: SnackbarHostState, viewModel: SharedViewModel, uiState: SharedUiState) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .width(totalWidth * 0.9f)
            .aspectRatio(1f / 0.16f)
            .background(Color.Red, shape = RoundedCornerShape(12.dp))
            .clickable {
                scope.launch {
                    // Firebase로 데이터 전송
                    viewModel.reportIllegalParking(uiState.signalRange, uiState.parkedStart)
                    // Snackbar를 띄우는 부분
                    snackbarHostState.showSnackbar(
                        message = "신고가 접수되었습니다.",
                        actionLabel = "확인",
                        duration = SnackbarDuration.Long,
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, style = TextStyle(fontSize = 23.sp, fontWeight = FontWeight.Bold, color = Color.White))
    }
}
