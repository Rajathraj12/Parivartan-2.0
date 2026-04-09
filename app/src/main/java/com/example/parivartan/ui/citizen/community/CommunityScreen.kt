package com.example.parivartan.ui.citizen.community

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import androidx.compose.material.icons.outlined.KeyboardArrowUp

data class Issue(
    val id: String,
    val title: String,
    val description: String,
    val status: String,
    val location: String,
    val category: String,
    val date: String,
    val upvotes: Int,
    val userName: String
)

val allMockIssues = listOf(
    Issue("1", "Broken Streetlight", "Streetlight on Main St is out.", "pending", "Main St", "infrastructure", "2026-03-29", 15, "Alice"),
    Issue("2", "Pothole", "Large pothole on 5th Ave.", "in-progress", "5th Ave", "roads", "2026-03-28", 22, "Bob"),
    Issue("3", "Trash Overflow", "Garbage bin overflowing.", "resolved", "Central Park", "sanitation", "2026-03-25", 5, "Charlie"),
    Issue("4", "Water Leakage", "Pipe burst near the school.", "pending", "Model Town", "sanitation", "2026-04-09", 34, "David"),
    Issue("5", "Fallen Tree", "Tree blocking the road after storm.", "in-progress", "Defence Colony", "parks", "2026-04-10", 45, "Eve"),
    Issue("6", "Stray Dogs", "Pack of aggressive stray dogs.", "pending", "Urban Estate", "infrastructure", "2026-04-08", 12, "Frank")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    onNavigateToMap: () -> Unit,
    onNavigateToIssueDetail: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf("all") }
    var refreshing by remember { mutableStateOf(false) }

    val email = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.email
    val showMock = email == "android@gmail.com"
    val mockIssues = remember(showMock) { if (showMock) allMockIssues else emptyList() }

    val filters = listOf(
        "all" to "All",
        "pending" to "Pending",
        "in-progress" to "In Progress",
        "resolved" to "Resolved",
        "infrastructure" to "Infrastructure",
        "roads" to "Roads",
        "parks" to "Parks",
        "sanitation" to "Sanitation"
    )

    val currentIssues = mockIssues
        .filter { if (activeFilter == "all") true else it.status == activeFilter || it.category == activeFilter }
        .filter { it.title.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true) || it.location.contains(searchQuery, ignoreCase = true) }
        .sortedByDescending { it.upvotes }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)) // slate-50
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF14B8A6), Color(0xFF0D9488)) // teal-500 to teal-600
                    )
                )
                .padding(top = 40.dp, bottom = 20.dp, start = 20.dp, end = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Community Issues",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "View and track issues reported in your community",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }

        // Search Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F5F9)),
                placeholder = { Text("Search issues...", color = Color(0xFF64748B)) },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = "Search", tint = Color(0xFF64748B))
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF1F5F9),
                    unfocusedContainerColor = Color(0xFFF1F5F9),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0D9488))
                    .clickable { onNavigateToMap() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Place, contentDescription = "Map View", tint = Color.White)
            }
        }

        // Filters
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { (id, name) ->
                val isActive = activeFilter == id
                Text(
                    text = name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isActive) Color.White else Color(0xFF334155),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isActive) Color(0xFF0D9488) else Color(0xFFF1F5F9))
                        .clickable { activeFilter = id }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        // Issues List
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(currentIssues) { issue ->
                IssueCard(issue, onNavigateToIssueDetail = onNavigateToIssueDetail)
            }
            if (currentIssues.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Outlined.Search,
                            contentDescription = "No items",
                            modifier = Modifier.size(48.dp),
                            tint = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No issues found", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                        Text("Try changing your search or filters", fontSize = 14.sp, color = Color(0xFF64748B))
                    }
                }
            }
        }
    }
}

// Function to format the date correctly from ISO8601 (or YYYY-MM-DD in our mock) to "MMM d"
fun formatDate(dateString: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatter = SimpleDateFormat("MMM d", Locale.getDefault())
        parser.parse(dateString)?.let { formatter.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}

@Composable
fun IssueCard(issue: Issue, onNavigateToIssueDetail: (String) -> Unit) {
    val statusColor = when (issue.status) {
        "pending" -> Color(0xFFEAB308) // amber-500
        "in-progress" -> Color(0xFF3B82F6) // blue-500
        "resolved" -> Color(0xFF10B981) // emerald-500
        else -> Color(0xFF94A3B8) // slate-400
    }

    val statusText = when (issue.status) {
        "pending" -> "Pending"
        "in-progress" -> "In Progress"
        "resolved" -> "Resolved"
        else -> issue.status.capitalize(Locale.getDefault())
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { onNavigateToIssueDetail(issue.id) }
            .padding(16.dp)
    ) {
        Column {
            // Header: User Info & Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0891B2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = issue.userName.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = issue.userName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155)
                    )
                }
                Text(
                    text = formatDate(issue.date),
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title & Description
            Text(
                text = issue.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = issue.description,
                fontSize = 14.sp,
                color = Color(0xFF334155),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Footer: Location & Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Outlined.Place, contentDescription = "Location", modifier = Modifier.size(14.dp), tint = Color(0xFF64748B))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = issue.location,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = statusText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(statusColor)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.KeyboardArrowUp, contentDescription = "Upvote", modifier = Modifier.size(16.dp), tint = Color(0xFF64748B))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = issue.upvotes.toString(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }
    }
}