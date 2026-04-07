package com.example.parivartan.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository : AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // User is signed in
                _authState.value = AuthState.Authenticated(displayName = user.displayName ?: user.email)
            } else {
                // User is signed out
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    override fun signIn(displayName: String?) {
        // This abstraction is too simple for real auth.
        // For Google/Email sign in, you would usually pass credentials or use UI.
        // We will keep this as a fallback to fake an auth state if needed, but normally use Firebase Auth callbacks.
        val user = auth.currentUser
        if (user != null) {
            _authState.value = AuthState.Authenticated(displayName = user.displayName ?: user.email)
        } else {
            // For now, simple mock if needed, or do nothing.
             _authState.value = AuthState.Authenticated(displayName = displayName)
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String): Result<Unit> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun signOut() {
        auth.signOut()
    }
}
