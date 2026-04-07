package com.example.parivartan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.FirebaseApp
import com.example.parivartan.auth.FirebaseAuthRepository
import com.example.parivartan.navigation.ParivartanApp
import com.example.parivartan.ui.theme.ParivartanTheme

class MainActivity : ComponentActivity() {
    private lateinit var authRepository: FirebaseAuthRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )

        authRepository = FirebaseAuthRepository()

        enableEdgeToEdge()
        setContent {
            ParivartanTheme {
                ParivartanApp(authRepository = authRepository)
            }
        }
    }
}