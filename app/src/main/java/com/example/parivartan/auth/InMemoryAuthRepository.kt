package com.example.parivartan.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryAuthRepository : AuthRepository {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        // Simulate quick session restore.
        _authState.value = AuthState.Unauthenticated
    }

    override fun signIn(displayName: String?) {
        _authState.value = AuthState.Authenticated(displayName = displayName)
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun signInWithEmailAndRole(email: String, password: String, expectedRole: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun signUpWithEmail(fullName: String, email: String, password: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun signInWithGoogleToken(idToken: String): Result<Unit> {
        return Result.success(Unit)
    }

    override fun signOut() {
        _authState.value = AuthState.Unauthenticated
    }
}