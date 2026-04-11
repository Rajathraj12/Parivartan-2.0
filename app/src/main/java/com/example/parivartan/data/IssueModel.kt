package com.example.parivartan.data

data class IssueModel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val department: String = "",
    val status: String = "pending",
    val priority: String = "low", // high, medium, low
    val locationAddress: String = "",
    val locationLat: Double = 0.0,
    val locationLng: Double = 0.0,
    val upvotes: Int = 0,
    val reporterId: String = "",
    val reporterName: String = "",
    val reporterContact: String = "",
    val sharedWithCommunity: Boolean = true,
    val assignedTo: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val photos: List<String> = emptyList(),
    val comments: List<CommentModel> = emptyList()
)

data class CommentModel(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
