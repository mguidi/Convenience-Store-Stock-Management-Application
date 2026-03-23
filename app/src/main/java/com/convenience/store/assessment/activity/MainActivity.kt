package com.convenience.store.assessment.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.convenience.store.assessment.navigation.ui.screens.NavigationScreen
import com.convenience.store.core.ui.theme.ConvenienceStoreAssessmentTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConvenienceStoreAssessmentTheme {
                NavigationScreen()
            }
        }
    }
}