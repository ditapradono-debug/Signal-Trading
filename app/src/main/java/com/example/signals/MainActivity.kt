package com.example.signals

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: MainViewModel = viewModel(factory = MainViewModelFactory("20137200-d8a5-5fab-db44-a74ac4cf"))
            MainScreen(vm)
        }
    }
}
