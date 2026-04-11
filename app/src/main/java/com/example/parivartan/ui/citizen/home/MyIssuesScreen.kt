package com.example.parivartan.ui.citizen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parivartan.data.IssueModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyIssuesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToIssueDetail: (String) -> Unit
) {
    var issues by remember { mutableStateOf<List<IssueModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            try {
                // Try from "issues" collection first
                val resultIssues = FirebaseFirestore.getInstance()
                    .collection("issues")
                    .whereEqualTo("reporterId", user.uid)
                    .get()
                    .await()

                var userIssues = resultIssues.mapNotNull { it.toObject(IssueModel::class.java).copy(id = it.id) }

                // Fallback to "grievances" if needed, just in case they store in both
                if (userIssues.isEmpty()) {
                     val resultGrievances = FirebaseFirestore.getInstance()
                        .collection("issues")
                        .whereEqualTo("userId", user.uid)
                        .get()
                        .await()

                     val userGrievances = resultGrievances.mapNotNull { doc ->
                         IssueModel(
                             id = doc.id,
                             title = doc.getString("title") ?: "No Title",
                             description = doc.getString("description") ?: "",
                             department = doc.getString("department") ?: "Unknown",
                             status = doc.getString("status") ?: "PENDING",
                             priority = doc.getString("priority") ?: "MEDIUM",
                             reporterName = doc.getString("userName") ?: doc.getString("reportedBy") ?: "Citizen",
                             createdAt = doc.getLong("timestamp") ?: System.currentTimeMillis()
                         )
                     }
                     userIssues = userGrievances
                }
                issues = userIssues.sortedByDescending { it.createdAt }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Complaints", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF0D9488))
            }
        } else if (issues.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("You haven't reported any issues yet.", color = Color(0xFF64748B))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(16.dp)) }
                items(issues.size) { index ->
                    val issue = issues[index]
                    MyIssueCard(issue, onClick = { onNavigateToIssueDetail(issue.id) })
                }
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
private fun MyIssueCard(issue: IssueModel, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID: ${issue.id.take(8).uppercase()}",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = formatTimestamp(issue.createdAt),
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = issue.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF111827),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = issue.department,
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IssueStatusChip(issue.status)
                Spacer(modifier = Modifier.width(8.dp))
                IssuePriorityChip(issue.priority)
            }
        }
    }
}

@Composable
private fun IssueStatusChip(status: String) {
    val (bg, fg) = when (status.lowercase()) {
        "pending" -> Color(0xFFFACC15) to Color(0xFF713F12)
        "in-progress", "in progress", "assigned", "under review", "underreview" -> Color(0xFF9CA3AF) to Color.White
        "resolved", "closed" -> Color(0xFF22C55E) to Color.White
        "rejected" -> Color(0xFFEF4444) to Color.White
        else -> Color(0xFFE5E7EB) to Color(0xFF111827)
    }
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status.uppercase(),
            color = fg,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun IssuePriorityChip(priority: String) {
    val (bg, fg) = when (priority.lowercase()) {
        "high" -> Color(0xFFEF4444) to Color.White
        "medium" -> Color(0xFFF97316) to Color.White
        "low" -> Color(0xFF22C55E) to Color.White
        else -> Color(0xFFE5E7EB) to Color(0xFF111827)
    }
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = priority.uppercase(),
            color = fg,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

