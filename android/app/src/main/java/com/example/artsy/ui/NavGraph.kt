package com.example.artsy.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.artsy.viewmodel.SessionViewModel

@Composable
fun NavGraph(sessionViewModel: SessionViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                navController = navController,
                onSearchClick = { navController.navigate("search") },
                onLoginClick = { navController.navigate("login") },
                sessionViewModel = sessionViewModel
            )
        }
        composable("search") {
            SearchScreen(
                navController = navController,
                sessionViewModel = sessionViewModel
            )
        }

        composable("login") {
            LoginScreen(
                navController = navController,
                sessionViewModel = sessionViewModel
            )
        }
        composable("register") {
            RegisterScreen(navController, sessionViewModel)
        }
        composable("artistDetails/{artistId}") { backStackEntry ->
            val artistId = backStackEntry.arguments?.getString("artistId") ?: ""
            ArtistDetailsScreen(navController = navController, artistId = artistId,sessionViewModel = sessionViewModel)
        }
    }

}
