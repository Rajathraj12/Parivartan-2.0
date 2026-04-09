package com.example.parivartan.ui.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

// Reusing the same data structure logically, though typically this would be a shared model
private data class IssueItem(
    val id: String,
    val title: String,
    val description: String,
    val priority: String,
    val status: String,
    val locationAddress: String,
    val upvotes: Int,
    val photoCount: Int,
    val updatedAt: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffIssueListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToIssueDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf("all") }
    val email = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.email
    val showMock = email == "android@gmail.com" || email == "test@gmail.com"

    val mockIssues = remember(showMock) {
        if (!showMock) emptyList() else listOf(
            IssueItem("101", "Pothole on Main St", "Large pothole causing traffic", "high", "pending", "Main St, City Center", 15, 1, System.currentTimeMillis() - 3600000),
            IssueItem("102", "Street light not working", "Pitch dark at night", "medium", "in-progress", "Oak Avenue", 5, 0, System.currentTimeMillis() - 86400000),
            IssueItem("103", "Garbage collection missed", "Smells bad", "low", "resolved", "Pine Street", 0, 2, System.currentTimeMillis() - 172800000),
            IssueItem("104", "Water leakage", "Pipe broken", "high", "pending", "Market Road", 20, 3, System.currentTimeMillis())
        )
    }

    val filteredIssues = mockIssues.filter { issue ->
        val matchesSearch = issue.title.contains(searchQuery, ignoreCase = true) ||
                issue.description.contains(searchQuery, ignoreCase = true) ||
                issue.id.contains(searchQuery, ignoreCase = true)
        val matchesStatus = statusFilter == "all" || issue.status == statusFilter
        matchesSearch && matchesStatus
    }

    val statusOptions = listOf(
        Triple("all", "All", mockIssues.size),
        Triple("pending", "Pending", mockIssues.count { it.status == "pending" }),
        Triple("in-progress", "In Progress", mockIssues.count { it.status == "in-progress" }),
        Triple("resolved", "Resolved", mockIssues.count { it.status == "resolved" })
    )

    val getStatusColor = { status: String ->
        when (status) {
            "pending" -> Color(0xFFEAB308)
            "acknowledged" -> Color(0xFF3B82F6)
            "in-progress" -> Color(0xFF8B5CF6)
            "resolved" -> Color(0xFF10B981)
            "cannot-resolve" -> Color(0xFFEF4444)
            else -> Color(0xFF6B7280)
        }
    }

    val getPriorityColor = { priority: String ->
        when (priority) {
            "high" -> Color(0xFFEF4444)
            "medium" -> Color(0xFFEAB308)
            "low" -> Color(0xFF10B981)
            else -> Color(0xFF6B7280)
        }
    }

    val formatRelativeDate = { dateMillis: Long ->
        val diffTime = System.currentTimeMillis() - dateMillis
        val diffDays = diffTime / (1000 * 60 * 60 * 24)
        when (diffDays) {
            0L -> "Today"
            1L -> "Yesterday"
            in 2..6 -> "$diffDays days ago"
            else -> {
                val cal = Calendar.getInstance().apply { timeInMillis = dateMillis }
                "${cal.get(Calendar.DAY_OF_MONTH)}/${cal.get(Calendar.MONTH) + 1}/${cal.get(Calendar.YEAR)}"
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("All Issues", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search issues...", color = Color(0xFF64748B)) },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = Color(0xFF64748B)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color(0xFF64748B))
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .background(Color.White, RoundedCornerShape(8.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedBorderColor = Color(0xFF0D9488),
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            // Status Filter
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(statusOptions) { (value, label, count) ->
                    val isActive = statusFilter == value
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { statusFilter = value }
                            .background(if (isActive) Color(0xFF0D9488) else Color.White, RoundedCornerShape(20.dp))
                            .border(1.dp, if (isActive) Color(0xFF0D9488) else Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isActive) Color.White else Color(0xFF334155),
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .background(if (isActive) Color(0xFF0E8C7E) else Color(0xFFF1F5F9), RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                .defaultMinSize(minWidth = 24.dp)
                        ) {
                            Text(
                                text = count.toString(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isActive) Color.White else Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

            // Issue List
            if (filteredIssues.isNotEmpty()) {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredIssues) { issue ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { onNavigateToIssueDetail(issue.id) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(issue.id, fontSize = 12.sp, color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
                                    val pColor = getPriorityColor(issue.priority)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier
                                            .background(pColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Flag, contentDescription = null, tint = pColor, modifier = Modifier.size(12.dp))
                                        Text(issue.priority.uppercase(), fontSize = 10.sp, color = pColor, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Text(
                                    text = issue.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E293B),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )

                                Text(
                                    text = issue.description,
                                    fontSize = 14.sp,
                                    color = Color(0xFF64748B),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 20.sp,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(bottom = 12.dp)
                                ) {
                                    Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                                    Text(issue.locationAddress, fontSize = 13.sp, color = Color(0xFF64748B), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(getStatusColor(issue.status), RoundedCornerShape(12.dp))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            issue.status.replace("-", " ").replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                                            fontSize = 11.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Text(formatRelativeDate(issue.updatedAt), fontSize = 12.sp, color = Color(0xFF9CA3AF))
                                }

                                if (issue.photoCount > 0 || issue.upvotes > 0) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (issue.photoCount > 0) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Icon(Icons.Outlined.Image, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                                                Text("${issue.photoCount} photo(s)", fontSize = 12.sp, color = Color(0xFF64748B))
                                            }
                                        }
                                        if (issue.upvotes > 0) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Icon(Icons.Outlined.ThumbUp, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                                                Text("${issue.upvotes} upvotes", fontSize = 12.sp, color = Color(0xFF64748B))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Outlined.FolderOpen, contentDescription = null, tint = Color(0xFFD1D5DB), modifier = Modifier.size(64.dp))
                    Text("No issues found", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B), modifier = Modifier.padding(top = 16.dp))
                    Text(
                        if (searchQuery.isNotEmpty()) "Try adjusting your search" else "No issues match the selected filter",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

