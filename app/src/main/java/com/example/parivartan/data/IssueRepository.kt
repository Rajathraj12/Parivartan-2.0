package com.example.parivartan.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class IssueRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun submitIssue(issue: IssueModel): Result<Unit> {
        return try {
            val user = auth.currentUser
            if (user == null) {
                return Result.failure(Exception("User not authenticated"))
            }

            // Assign a new ID if it's empty
            val dbIssue = if (issue.id.isEmpty()) {
                val newDocRef = firestore.collection("issues").document()
                issue.copy(
                    id = newDocRef.id,
                    reporterId = user.uid,
                    reporterName = user.displayName ?: "Citizen",
                    reporterContact = user.email ?: ""
                )
            } else {
                issue
            }

            firestore.collection("issues").document(dbIssue.id).set(dbIssue).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

