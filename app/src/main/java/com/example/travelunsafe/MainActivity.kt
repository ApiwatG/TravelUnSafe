package com.example.travelunsafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.travelunsafe.ui.theme.TravelUnSafeTheme

class MainActivity : ComponentActivity() {
    private val viewModel: HotelsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TravelUnSafeTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MyScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MyScreen(viewModel: HotelsViewModel) {
    val navController = rememberNavController()
    NavGraph(navController = navController, viewModel = viewModel)
}