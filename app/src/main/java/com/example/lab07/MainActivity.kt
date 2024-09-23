package com.example.lab07

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.lab07.ui.theme.Lab07Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab07Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Reemplazamos la función Greeting con ScreenUser
                    ScreenUser(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
