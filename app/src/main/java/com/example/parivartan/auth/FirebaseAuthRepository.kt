package com.example.parivartan.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
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
    private val firestore = FirebaseFirestore.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private var isValidatingRole = false

    init {
        auth.addAuthStateListener { firebaseAuth ->
            if (isValidatingRole) return@addAuthStateListener
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

    override suspend fun signInWithEmailAndRole(email: String, password: String, expectedRole: String): Result<Unit> {
        if (!isNetworkAvailable()) {
            return Result.failure(Exception("No internet connection. Please check your network and try again."))
        }
        return try {
            isValidatingRole = true
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("User not found")

            // Check Firestore for user role
            val doc = firestore.collection("users").document(user.uid).get().await()

            // Derive the real role from email for demo accounts to self-heal bad DB state
            val emailLower = email.lowercase()
            val derivedRole = when {
                emailLower.startsWith("staff") -> "staff"
                emailLower.startsWith("admin") -> "admin"
                else -> {
                    val prefix = emailLower.substringBefore("@")
                    val knownDepartments = listOf("municipal", "traffic-police", "water-sanitation", "pspcl", "health-welfare", "civil-surgeon", "punjab-police", "education", "agriculture", "food-civil-supplies", "roadways", "rto", "revenue", "social-security", "pollution-control", "forest", "disaster-management", "pwd")
                    if (knownDepartments.contains(prefix)) prefix else null
                }
            }

            var actualRole = doc.getString("role")

            // Auto-heal or bootstrap missing roles
            if ((derivedRole != null && actualRole != derivedRole) || actualRole == null) {
                actualRole = derivedRole ?: "citizen"
                val fallbackData = mutableMapOf<String, Any>(
                    "uid" to user.uid,
                    "email" to email,
                    "role" to actualRole
                )
                firestore.collection("users").document(user.uid).set(fallbackData).await()
            }

            // Check if actual role matches expected (either exact match, or 'department' vs 'department:pwd')
            // To be secure, the DB should store "department:pwd", not just "department"
            var roleValid = expectedRole == actualRole
            if (expectedRole.startsWith("department:")) {
                val dbRole = expectedRole.substringAfter("department:")
                if (actualRole == dbRole) {
                    roleValid = true
                }
            }

            if (!roleValid) {
                // If invalid role, simply throw error without logging in
                auth.signOut()
                // Wait briefly so any async callback from Firebase triggers while validation is still active,
                // preventing it from resetting _authState and causing a redirect to Intro
                kotlinx.coroutines.delay(300)
                isValidatingRole = false
                throw Exception("Please enter a valid user email for this portal.")
            }

            isValidatingRole = false
            _authState.value = AuthState.Authenticated(displayName = user.displayName ?: user.email)
            Result.success(Unit)
        } catch (e: Exception) {
            isValidatingRole = false
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

            // Save user profile in Firestore with default "citizen" role
            if (user != null) {
                val userData = mapOf(
                    "uid" to user.uid,
                    "name" to fullName,
                    "email" to email,
                    "role" to "citizen",
                    "createdAt" to System.currentTimeMillis()
                )
                firestore.collection("users").document(user.uid).set(userData).await()
            }

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
