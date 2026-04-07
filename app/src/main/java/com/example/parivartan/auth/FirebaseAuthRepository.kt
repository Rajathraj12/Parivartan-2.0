package com.example.parivartan.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class FirebaseAuthRepository : AuthRepository {
    private val auth = FirebaseAuth.getInstance().apply {
        firebaseAuthSettings.setAppVerificationDisabledForTesting(true)
    }
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

    private fun isNetworkAvailable(): Boolean {
        val context = auth.app.applicationContext
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
               activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
               activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> {
        if (!isNetworkAvailable()) {
            return Result.failure(Exception("No internet connection. Please check your network and try again."))
        }
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUpWithEmail(fullName: String, email: String, password: String): Result<Unit> {
        if (!isNetworkAvailable()) {
            return Result.failure(Exception("No internet connection. Please check your network and try again."))
        }
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(fullName)
                .build()
            user?.updateProfile(profileUpdates)?.await()
            Result.success(Unit)
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("DNS Error: Cannot connect to Firebase servers. Please fix device date/time or DNS settings.", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogleToken(idToken: String): Result<Unit> {
        if (!isNetworkAvailable()) {
            return Result.failure(Exception("No internet connection. Please check your network and try again."))
        }
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun signOut() {
        auth.signOut()
    }
}
