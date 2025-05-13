package com.example.movieapp.presentation.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.movieapp.ui.theme.background
import com.example.movieapp.util.Constants.netflixFamily

@Composable
fun PersonScreen(navController: NavController, personName: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to Person Screen",
            fontSize = 22.sp,
            fontFamily = netflixFamily,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
        Text(
            text = personName,
            fontSize = 15.sp,
            fontFamily = netflixFamily,
            fontWeight = FontWeight.Normal,
            color = Color.White
        )
    }
}