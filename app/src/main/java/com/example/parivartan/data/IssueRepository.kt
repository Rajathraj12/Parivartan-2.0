package com.example.parivartan.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class IssueRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun submitIssue(issue: IssueModel): Result<String> {
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
            Result.success(dbIssue.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getIssue(id: String): Result<IssueModel> {
        return try {
            val documentReference = firestore.collection("issues").document(id)
            val snapshot = documentReference.get().await()
            val issue = snapshot.toObject(IssueModel::class.java)
            if (issue != null) {
                Result.success(issue)
            } else {
                Result.failure(Exception("Issue not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateIssueStatus(id: String, newStatus: String): Result<Unit> {
        return try {
            firestore.collection("issues").document(id).update("status", newStatus, "updatedAt", System.currentTimeMillis()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateIssuePriority(id: String, newPriority: String): Result<Unit> {
        return try {
            firestore.collection("issues").document(id).update("priority", newPriority, "updatedAt", System.currentTimeMillis()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getIssuesByDepartment(departmentId: String): Result<List<IssueModel>> {
        return try {
            val snapshot = firestore.collection("issues")
                .whereEqualTo("department", departmentId)
                .get()
                .await()
            val issues = snapshot.documents.mapNotNull { it.toObject(IssueModel::class.java) }
            Result.success(issues)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getIssuesAssignedToStaff(staffName: String): Result<List<IssueModel>> {
        return try {
            val snapshot = firestore.collection("issues")
                .whereEqualTo("assignedTo", staffName)
                .get()
                .await()
            val issues = snapshot.documents.mapNotNull { it.toObject(IssueModel::class.java) }
            Result.success(issues)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllIssues(): Result<List<IssueModel>> {
        return try {
            val snapshot = firestore.collection("issues").get().await()
            val issues = snapshot.documents.mapNotNull { it.toObject(IssueModel::class.java) }
            Result.success(issues)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun assignIssueToStaff(id: String, staffName: String): Result<Unit> {
        return try {
            val updateData = mapOf(
                "assignedTo" to staffName,
                "status" to "assigned",
                "updatedAt" to System.currentTimeMillis()
            )
            firestore.collection("issues").document(id).update(updateData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
