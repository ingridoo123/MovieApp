package com.example.movieapp.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.movieapp.navigation.Screen
import com.example.movieapp.ui.theme.background
import com.example.movieapp.ui.theme.component
import com.example.movieapp.ui.theme.componentLighter
import com.example.movieapp.ui.theme.top_bar_component

@Composable
fun MovieBottomBar(navController: NavController, selectedTab: Int) {

    val items = listOf(
        Screen.Home to Icons.Default.Home,
        Screen.Search to Icons.Default.Search,
        Screen.Favourite to Icons.Default.BookmarkBorder
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .drawBehind {
                drawLine(
                    color = component,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 2.dp.toPx(),
                )
            }
            ,
        color = background.copy(alpha = 0.9f),
        tonalElevation = 5.dp,
        shadowElevation = 10.dp
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, (screen, icon) ->
                val isSelected = selectedTab == index

                IconButton(onClick = {
                    if(navController.currentDestination?.route != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }) {
                    Icon(
                        imageVector = icon,
                        contentDescription = screen.route,
                        tint = if(isSelected) Color.White.copy(0.8f) else component,
                        modifier = Modifier.size(35.dp)
                    )

                }


            }
        }
    }

}







