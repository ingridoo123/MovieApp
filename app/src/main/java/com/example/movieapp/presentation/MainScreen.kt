package com.example.movieapp.presentation

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.movieapp.presentation.components.MovieBottomBar
import com.example.movieapp.presentation.screens.favourite.FavouriteScreen
import com.example.movieapp.presentation.screens.home.HomeViewModel
import com.example.movieapp.presentation.screens.home.SimpleHomeScreen
import com.example.movieapp.presentation.screens.search.SearchScreen
import com.example.movieapp.ui.theme.background


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavController) {
    var selectedTab by rememberSaveable { mutableStateOf(0) }

    val homeViewModel: HomeViewModel = hiltViewModel()

    androidx.compose.material.Scaffold(
        bottomBar = {
            MovieBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        backgroundColor = background
    ) {
        Crossfade(targetState = selectedTab, label = "tab_switch") {
            when (it) {
                0 -> SimpleHomeScreen(navController = navController, viewModel = homeViewModel)
                1 -> SearchScreen(navController = navController)
                2 -> FavouriteScreen(navController = navController)
            }
        }
    }
}