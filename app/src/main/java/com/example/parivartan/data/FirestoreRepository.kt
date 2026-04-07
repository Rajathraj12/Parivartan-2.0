package com.example.parivartan.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun addData(collection: String, documentId: String, data: Map<String, Any>) {
        db.collection(collection).document(documentId).set(data).await()
    }

    suspend fun getData(collection: String, documentId: String): Map<String, Any>? {
        val documentSnapshot = db.collection(collection).document(documentId).get().await()
        return if (documentSnapshot.exists()) {
            documentSnapshot.data
        } else {
            null
        }
    }

    suspend fun updateData(collection: String, documentId: String, field: String, value: Any) {
        db.collection(collection).document(documentId).update(field, value).await()
    }

    suspend fun deleteData(collection: String, documentId: String) {
        db.collection(collection).document(documentId).delete().await()
    }
}

