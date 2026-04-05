package com.example.parivartan.auth

sealed interface AuthState {
    data object Loading : AuthState
    data object Unauthenticated : AuthState
    data class Authenticated(val displayName: String? = null) : AuthState
}