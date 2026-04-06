package com.example.parivartan.ui.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StaffDashboardScreen(
    onNavigateIssues: () -> Unit,
    onNavigateMap: () -> Unit,
    onNavigateNotifications: () -> Unit,
    onNavigateSettings: () -> Unit,
    onNavigateToIssueDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val stats = mapOf(
        "total" to 42,
        "pending" to 15,
        "inProgress" to 10,
        "resolved" to 17
    )

    // Dummy data for recent issues
    val recentIssues = listOf(
        Issue("101", "Pothole on Main St", "high", "pending", "Main St, City Center"),
        Issue("102", "Street light not working", "medium", "in-progress", "Oak Avenue"),
        Issue("103", "Garbage collection missed", "low", "resolved", "Pine Street")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
    ) {
        // Profile Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(Color(0xFF14B8A6), Color(0xFF0D9488))))
                .padding(top = 60.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.White.copy(alpha = 0.2f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                    Text("Welcome back,", color = Color(0xCCFFFFFF), fontSize = 14.sp)
                    Text(
                        text = "John Doe",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Text("Public Works Dept", color = Color(0xCCFFFFFF), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))

                    Box(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("ID: PWD-1042", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Box(contentAlignment = Alignment.TopEnd) {
                    IconButton(onClick = onNavigateNotifications) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(top = 6.dp, end = 6.dp)
                            .size(10.dp)
                            .background(Color(0xFFF59E0B), CircleShape)
                            .border(1.dp, Color(0xFF0D9488), CircleShape)
                    )
                }
            }
        }

        // Stats Grid
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Inventory,
                    label = "Total Issues",
                    value = stats["total"].toString(),
                    color = Color(0xFF0D9488)
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.AccessTime,
                    label = "Pending",
                    value = stats["pending"].toString(),
                    color = Color(0xFFEAB308)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.RocketLaunch,
                    label = "In Progress",
                    value = stats["inProgress"].toString(),
                    color = Color(0xFF3B82F6)
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.CheckCircle,
                    label = "Resolved",
                    value = stats["resolved"].toString(),
                    color = Color(0xFF10B981)
                )
            }
        }

        // Recent Activity
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recent Activity", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Text(
                    "See All",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0D9488),
                    modifier = Modifier.clickable { onNavigateIssues() }
                )
            }

            // Dummy check for empty logic explicitly to avoid compiler warnings
            val hasRecentIssues = recentIssues.isNotEmpty()
            if (hasRecentIssues) {
                recentIssues.forEach { issue ->
                    IssueCard(issue = issue, onClick = { onNavigateToIssueDetail(issue.id) })
                    Spacer(modifier = Modifier.height(12.dp))
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FolderOpen,
                        contentDescription = "Empty",
                        tint = Color(0xFFD1D5DB),
                        modifier = Modifier.size(48.dp)
                    )
                    Text("No recent issues", color = Color(0xFF9CA3AF), fontSize = 14.sp, modifier = Modifier.padding(top = 12.dp))
                }
            }
        }

        // Quick Actions
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Quick Actions", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), modifier = Modifier.padding(bottom = 16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionButton(
                    icon = Icons.AutoMirrored.Outlined.List,
                    label = "View All",
                    onClick = onNavigateIssues,
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    icon = Icons.Outlined.Map,
                    label = "Map View",
                    onClick = onNavigateMap,
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(

                    icon = androidx.compose.material.icons.Icons.Outlined.Settings,
                    label = "Settings",
                    onClick = onNavigateSettings,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 0.dp, color = Color.Transparent)
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 4.dp)
                    .background(color)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(24.dp).padding(bottom = 12.dp)
                    )
                    Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                    Text(label, fontSize = 12.sp, color = Color(0xFF6B7280), modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}

@Composable
private fun IssueCard(
    issue: Issue,
    onClick: () -> Unit
) {
    val statusColor = when (issue.status) {
        "pending" -> Color(0xFFEAB308)
        "in-progress" -> Color(0xFF3B82F6)
        "resolved" -> Color(0xFF10B981)
        "cannot-resolve" -> Color(0xFFEF4444)
        else -> Color(0xFF6B7280)
    }

    val priorityColor = when (issue.priority) {
        "high" -> Color(0xFFEF4444)
        "medium" -> Color(0xFFEAB308)
        "low" -> Color(0xFF10B981)
        else -> Color(0xFF6B7280)
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("#${issue.id}", fontSize = 12.sp, color = Color(0xFF6B7280), fontWeight = FontWeight.SemiBold)
                Box(
                    modifier = Modifier
                        .background(priorityColor.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(issue.priority.uppercase(), color = priorityColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Text(
                text = issue.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF111827),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(statusColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        issue.status.replace("-", " ").replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() },
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(14.dp))
                    Text(
                        issue.location.split(",").first(),
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(start = 4.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp).fillMaxWidth()
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = Color(0xFF0D9488), modifier = Modifier.size(24.dp))
            Text(
                label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF334155),
                modifier = Modifier.padding(top = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

private data class Issue(
    val id: String,
    val title: String,
    val priority: String,
    val status: String,
    val location: String
)
