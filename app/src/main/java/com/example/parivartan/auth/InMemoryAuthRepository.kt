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

    override fun signOut() {
        _authState.value = AuthState.Unauthenticated
    }
}

