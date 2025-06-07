package com.ram.kewps_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ram.kewps_3.ui.screens.KewPS3App
import com.ram.kewps_3.ui.theme.KewPS3Theme
import com.ram.kewps_3.viewmodel.KewPS3ViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KewPS3Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    KewPS3App(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel<KewPS3ViewModel>()
                    )
                }
            }
        }
    }
}