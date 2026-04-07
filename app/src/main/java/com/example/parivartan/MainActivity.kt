package com.example.parivartan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.parivartan.auth.FirebaseAuthRepository
import com.example.parivartan.navigation.ParivartanApp
import com.example.parivartan.ui.theme.ParivartanTheme

class MainActivity : ComponentActivity() {
    private val authRepository = FirebaseAuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ParivartanTheme {
                ParivartanApp(authRepository = authRepository)
            }
        }
    }
}