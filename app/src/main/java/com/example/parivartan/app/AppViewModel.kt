package com.example.parivartan.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parivartan.auth.AuthRepository
import com.example.parivartan.auth.AuthState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface InitState {
    data object Initializing : InitState
    data object Unauthenticated : InitState
    data class Authenticated(val displayName: String?) : InitState
}

class AppViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    val initState: StateFlow<InitState> = authRepository.authState
        .map { authState ->
            when (authState) {
                AuthState.Loading -> InitState.Initializing
                AuthState.Unauthenticated -> InitState.Unauthenticated
                is AuthState.Authenticated -> InitState.Authenticated(authState.displayName)
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, InitState.Initializing)

    /**
     * Mirrors the RN demo initializer pattern.
     * Put any one-time setup here.
     */
    fun initialize() {
        viewModelScope.launch {
            // Small delay so Splash isn't a flash.
            delay(350)
        }
    }

    fun signInDemo() = authRepository.signIn(displayName = "Citizen")

    fun signOut() = authRepository.signOut()
}

