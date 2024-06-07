package com.example.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


@Composable
fun NavigationScreen(sharedViewModel: SharedViewModel) {
    var navigationSelectedItem by remember { mutableIntStateOf(2) }
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // 현재 경로를 가져옴
    val currentRoute = navBackStackEntry?.destination?.route

    // 뒤로가기 할 때 선택된 아이템 업데이트
    navController.addOnDestinationChangedListener { _, destination, _ ->
        BottomNavigationItem().bottomNavigationItems().forEachIndexed { index, navigationItem ->
            if (destination.route == navigationItem.route) {
                navigationSelectedItem = index
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                BottomNavigationItem().bottomNavigationItems()
                    .forEachIndexed { index, navigationItem ->
                        // 하단 네비게이션 바에서 필수적인 요소
                        NavigationBarItem(
                            selected = index == navigationSelectedItem,
                            label = {
                                Text(
                                    navigationItem.label,
                                    fontWeight = FontWeight.Normal
                                )
                            },
                            icon = {
                                Icon(
                                    navigationItem.icon,
                                    contentDescription = navigationItem.label
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black, // 선택된 아이콘의 색상
                                unselectedIconColor = Color.LightGray, // 비선택된 아이콘의 색상
                                selectedTextColor = Color.Black, // 선택된 텍스트의 색상
                                unselectedTextColor = Color.LightGray, // 비선택된 텍스트의 색상
                                indicatorColor = Color.Transparent// 선택된 아이콘에 생기는 그림자의 색상 = 없음
                            ),
                            onClick = {
                                navigationSelectedItem = index
                                // NavHost에서 정의한 루트와 연결된 함수로 이동
                                navController.navigate(navigationItem.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screens.Home.route,
            modifier = Modifier.padding(paddingValues = paddingValues)) {
            composable(Screens.Home.route) {
                HomeScreen(navController, sharedViewModel)
            }
            composable(Screens.Detect.route) {
                DetectScreen(navController, sharedViewModel)
            }
            composable(Screens.Report.route){
                ReportScreen(navController, sharedViewModel)
            }
        }
    }
}
