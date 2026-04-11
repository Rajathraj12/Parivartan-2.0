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

import kotlinx.coroutines.tasks.await

sealed interface InitState {
    data object Initializing : InitState
    data object Unauthenticated : InitState
    data class Authenticated(val displayName: String?, val role: String = "citizen") : InitState
}

class AppViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    var currentRole: String = "citizen"
        private set

    val initState: StateFlow<InitState> = authRepository.authState
        .map { authState ->
            when (authState) {
                AuthState.Loading -> InitState.Initializing
                AuthState.Unauthenticated -> InitState.Unauthenticated
                is AuthState.Authenticated -> {
                    var roleToEmit = "citizen"
                    val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        try {
                            var role = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(user.uid)
                                .get()
                                .await()
                                .getString("role") ?: "citizen"

                            // To match navigation logic, if role is a department name without the prefix, add it
                            val nonDepartmentRoles = listOf("citizen", "staff", "admin")
                            if (!nonDepartmentRoles.contains(role) && !role.startsWith("department:")) {
                                role = "department:$role"
                            }

                            roleToEmit = role
                            currentRole = role
                        } catch (e: Exception) {
                            // Default to currentRole or citizen
                            roleToEmit = currentRole
                        }
                    } else {
                        roleToEmit = currentRole
                    }
                    InitState.Authenticated(authState.displayName, roleToEmit)
                }
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

    fun signInDemo(role: String = "citizen") {
        currentRole = role
        authRepository.signIn(displayName = role.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() })
    }

    fun signInWithEmail(email: String, password: String, role: String, onError: (String) -> Unit) {
        viewModelScope.launch {
            currentRole = role
            val result = authRepository.signInWithEmailAndRole(email, password, role)
            if (result.isFailure) {
                onError(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun signInWithGoogle(idToken: String, role: String, onError: (String) -> Unit) {
        viewModelScope.launch {
            currentRole = role
            val result = authRepository.signInWithGoogleToken(idToken)
            if (result.isFailure) {
                onError(result.exceptionOrNull()?.message ?: "Google Sign-in failed")
            }
        }
    }

    fun signUpWithEmail(fullName: String, email: String, password: String, role: String, onError: (String) -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            currentRole = role
            val result = authRepository.signUpWithEmail(fullName, email, password)
            if (result.isSuccess) {
                authRepository.signIn(displayName = fullName)
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Signup failed")
            }
        }
    }

    fun signOut() = authRepository.signOut()
}