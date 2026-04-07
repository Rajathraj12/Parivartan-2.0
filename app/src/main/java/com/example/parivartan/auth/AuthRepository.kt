package com.example.parivartan.auth

import kotlinx.coroutines.flow.StateFlow

/**
 * Minimal auth abstraction to drive navigation.
 * Replace this with Firebase/Auth0/etc later.
 */
interface AuthRepository {
    val authState: StateFlow<AuthState>

    fun signIn(displayName: String? = null)
    suspend fun signInWithEmail(email: String, password: String): Result<Unit>
    suspend fun signUpWithEmail(fullName: String, email: String, password: String): Result<Unit>
    suspend fun signInWithGoogleToken(idToken: String): Result<Unit>
    fun signOut()
}