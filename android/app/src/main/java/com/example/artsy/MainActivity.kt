package com.example.artsy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.artsy.ui.NavGraph
import androidx.compose.ui.tooling.preview.Preview
import com.example.artsy.ui.theme.ArtsyTheme
import com.example.artsy.ui.HomeScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.artsy.viewmodel.SessionViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Artsy)
        super.onCreate(savedInstanceState)

        com.example.artsy.network.RetrofitInstance.init(applicationContext)

        enableEdgeToEdge()
        setContent {
            val sessionViewModel: SessionViewModel = viewModel()

            LaunchedEffect(Unit) {
                sessionViewModel.restoreLoginIfSessionValid()
            }

            ArtsyTheme {
                NavGraph(sessionViewModel = sessionViewModel)
            }
        }

    }
}
