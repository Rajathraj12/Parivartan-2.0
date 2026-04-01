package com.example.parivartan.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

private val Teal500 = Color(0xFF14B8A6)
private val Teal600 = Color(0xFF0D9488)
private val Slate50 = Color(0xFFF8FAFC)
private val Slate500 = Color(0xFF64748B)
private val Slate700 = Color(0xFF334155)
private val Slate800 = Color(0xFF1E293B)

private val Cyan50 = Color(0xFFECFEFF)
private val Cyan700 = Color(0xFF0E7490)
private val Cyan800 = Color(0xFF164E63)

private data class Issue(
    val id: String,
    val title: String,
    val status: Status,
    val district: String,
    val address: String? = null,
    val upvotes: Int = 0,
)

private enum class Status {
    Pending,
    UnderReview,
    Assigned,
    InProgress,
    Resolved,
    Rejected,
    Closed
}

private fun statusColor(status: Status): Color = when (status) {
    Status.Pending -> Color(0xFFEAB308)
    Status.UnderReview -> Color(0xFFF59E0B)
    Status.Assigned -> Color(0xFF8B5CF6)
    Status.InProgress -> Color(0xFF3B82F6)
    Status.Resolved -> Color(0xFF10B981)
    Status.Rejected -> Color(0xFFEF4444)
    Status.Closed -> Color(0xFF6B7280)
}

private fun statusText(status: Status): String = when (status) {
    Status.Pending -> "Pending"
    Status.UnderReview -> "Under Review"
    Status.Assigned -> "Assigned"
    Status.InProgress -> "In Progress"
    Status.Resolved -> "Resolved"
    Status.Rejected -> "Rejected"
    Status.Closed -> "Closed"
}

@Composable
fun HomeScreen(
    userFirstName: String,
    onOpenProfile: () -> Unit,
    onReportIssue: () -> Unit,
    onOpenMyComplaints: () -> Unit,
    onOpenMap: () -> Unit = {},
    onOpenCommunity: () -> Unit = {},
    onOpenIssueDetail: (issueId: String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    // Mock data for now (no auth/backend as requested)
    val urgentIssues = remember {
        listOf(
            Issue("1", "Garbage overflow near market", Status.Pending, "Central", upvotes = 42),
            Issue("2", "Pothole on main road", Status.InProgress, "North", upvotes = 31),
            Issue("3", "Streetlight not working", Status.UnderReview, "East", upvotes = 27),
            Issue("4", "Water leakage", Status.Assigned, "West", upvotes = 18),
            Issue("5", "Illegal dumping", Status.Rejected, "South", upvotes = 12),
        )
    }

    val recentIssues = remember {
        listOf(
            Issue("11", "Broken sidewalk", Status.Pending, "Central", upvotes = 3),
            Issue("12", "Stray dog complaint", Status.UnderReview, "East", upvotes = 7),
            Issue("13", "Open manhole", Status.InProgress, "North", upvotes = 11),
            Issue("14", "Graffiti on wall", Status.Closed, "West", upvotes = 1),
            Issue("15", "Blocked drain", Status.Resolved, "South", upvotes = 5),
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
    ) {
        item {
            Header(
                userFirstName = userFirstName,
                onOpenProfile = onOpenProfile,
            )
        }

        item {
            QuickActionsRow(
                onReport = onReportIssue,
                onMap = onOpenMap,
                onMyIssues = onOpenMyComplaints,
                onCommunity = onOpenCommunity,
            )
        }

        item {
            SectionHeader(
                title = "Urgent Issues",
                actionText = "See All",
                onActionClick = onOpenMap,
                modifier = Modifier.padding(top = 24.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 16.dp, end = 16.dp)
            ) {
                items(urgentIssues, key = { it.id }) { issue ->
                    UrgentIssueCard(
                        issue = issue,
                        onClick = { onOpenIssueDetail(issue.id) },
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            }
        }

        item {
            SectionHeader(
                title = "Recent Reports",
                actionText = "See All",
                onActionClick = onOpenCommunity,
                modifier = Modifier.padding(top = 24.dp)
            )
        }

        items(recentIssues, key = { it.id }) { issue ->
            RecentIssueRow(
                issue = issue,
                onClick = { onOpenIssueDetail(issue.id) },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp)
            )
        }

        item {
            TipsCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp, bottom = 40.dp)
            )
        }
    }
}

@Composable
private fun Header(
    userFirstName: String,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Teal500, Teal600)))
            .padding(top = 60.dp, bottom = 30.dp, start = 20.dp, end = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = if (userFirstName.isBlank()) "Welcome!" else "Welcome, $userFirstName!",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Report and track community issues",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0891B2))
                    .clickable(onClick = onOpenProfile),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (userFirstName.firstOrNull()?.uppercaseChar()?.toString() ?: "U"),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun QuickActionsRow(
    onReport: () -> Unit,
    onMap: () -> Unit,
    onMyIssues: () -> Unit,
    onCommunity: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        QuickAction(icon = Icons.Outlined.AddCircle, title = "Report", color = Teal600, onClick = onReport)
        QuickAction(icon = Icons.Outlined.Info, title = "Map View", color = Color(0xFF3B82F6), onClick = onMap)
        QuickAction(icon = Icons.Outlined.Info, title = "My Issues", color = Color(0xFF8B5CF6), onClick = onMyIssues)
        QuickAction(icon = Icons.Outlined.Person, title = "Community", color = Color(0xFFF59E0B), onClick = onCommunity)
    }
}

@Composable
private fun QuickAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(72.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = Slate700,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionText: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Slate800
        )
        Text(
            text = actionText,
            color = Teal600,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable(onClick = onActionClick)
        )
    }
}

@Composable
private fun UrgentIssueCard(
    issue: Issue,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .width(230.dp)
            .clickable(onClick = onClick),
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
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(statusColor(issue.status))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText(issue.status),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "↑ ${issue.upvotes}",
                    color = Slate500,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = issue.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Slate800,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "📍 ${issue.address ?: issue.district}",
                color = Slate500,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun RecentIssueRow(
    issue: Issue,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(statusColor(issue.status))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = issue.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = Slate800,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "📍 ${issue.address ?: issue.district}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "↑ ${issue.upvotes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = ">", color = Color(0xFF94A3B8))
            }
        }
    }
}

@Composable
private fun TipsCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Cyan50),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "💡", style = MaterialTheme.typography.titleMedium, color = Teal600)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Report Issues Faster",
                    color = Cyan700,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Enable location access to automatically detect your location when reporting issues.",
                    color = Cyan800,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
