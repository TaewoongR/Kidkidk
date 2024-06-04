package com.example.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Create
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screens(val route : String) {
    data object Home : Screens("home_route")
    data object Detect : Screens("detect_route")
    data object Report : Screens("report_route")
}

data class BottomNavigationItem(
    val label : String = "",
    val icon : ImageVector = Icons.Filled.Home,
    val route : String = ""
) {
    fun bottomNavigationItems() : List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                label = "감지",
                icon = Icons.Filled.Place,
                route = Screens.Detect.route

            ),
            BottomNavigationItem(
                label = "홈",
                icon = Icons.Rounded.Create,
                route = Screens.Home.route
            ),
            BottomNavigationItem(
                label = "신고",
                icon = Icons.Filled.Warning,
                route = Screens.Report.route
            ),
        )
    }
}