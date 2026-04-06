package com.example.parivartan.ui.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class StaffNotification(
    val id: String,
    val type: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val read: Boolean,
    val issueId: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffNotificationsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToIssueDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var notifications by remember {
        mutableStateOf(
            listOf(
                StaffNotification(
                    id = "n1",
                    type = "assignment",
                    title = "New Issue Assigned",
                    message = "You have been assigned to issue #101: Pothole on Main St. Please review and take action.",
                    timestamp = System.currentTimeMillis() - 15 * 60 * 1000, // 15 mins ago
                    read = false,
                    issueId = "101"
                ),
                StaffNotification(
                    id = "n2",
                    type = "deadline",
                    title = "Resolution Deadline Approaching",
                    message = "Issue #102: Street light not working requires resolution within 24 hours.",
                    timestamp = System.currentTimeMillis() - 3 * 3600 * 1000, // 3 hours ago
                    read = false,
                    issueId = "102"
                ),
                StaffNotification(
                    id = "n3",
                    type = "comment",
                    title = "New Comment",
                    message = "Citizen added a new comment to issue #103: Garbage collection missed.",
                    timestamp = System.currentTimeMillis() - 25 * 3600 * 1000, // 1 day ago
                    read = true,
                    issueId = "103"
                )
            )
        )
    }

    var notificationToDelete by remember { mutableStateOf<String?>(null) }

    val unreadCount = notifications.count { !it.read }

    val getIcon = { type: String ->
        when (type) {
            "assignment" -> Icons.AutoMirrored.Filled.Article
            "deadline" -> Icons.Outlined.Schedule
            "comment" -> Icons.Outlined.ChatBubbleOutline
            else -> Icons.AutoMirrored.Filled.Article
        }
    }

    val getColor = { type: String ->
        when (type) {
            "assignment" -> Color(0xFF0D9488) // Teal
            "deadline" -> Color(0xFFF59E0B) // Amber
            "comment" -> Color(0xFF8B5CF6) // Purple
            else -> Color(0xFF64748B) // Gray
        }
    }

    val formatTimestamp = { timestamp: Long ->
        val diffMs = System.currentTimeMillis() - timestamp
        val diffMins = diffMs / 60000
        val diffHours = diffMs / 3600000
        val diffDays = diffMs / 86400000

        when {
            diffMins < 1 -> "Just now"
            diffMins < 60 -> "${diffMins}m ago"
            diffHours < 24 -> "${diffHours}h ago"
            diffDays < 7 -> "${diffDays}d ago"
            else -> {
                val date = java.util.Date(timestamp)
                val format = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
                format.format(date)
            }
        }
    }

    val handleMarkAllRead = {
        notifications = notifications.map { it.copy(read = true) }
    }

    val handleMarkAsRead = { id: String ->
        notifications = notifications.map { if (it.id == id) it.copy(read = true) else it }
    }

    val handleNotificationPress = { notification: StaffNotification ->
        handleMarkAsRead(notification.id)
        if (notification.issueId != null) {
            onNavigateToIssueDetail(notification.issueId)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Notifications", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        if (unreadCount > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFEF4444), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(unreadCount.toString(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (notifications.isNotEmpty() && unreadCount > 0) {
                        TextButton(onClick = handleMarkAllRead) {
                            Text("Mark all read", color = Color(0xFF0D9488), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (notifications.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.NotificationsOff, contentDescription = null, tint = Color(0xFFD1D5DB), modifier = Modifier.size(64.dp))
                    Text(
                        "No notifications",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B),
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        "You're all caught up! Check back later for updates.",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(notifications) { notification ->
                        val color = getColor(notification.type)
                        val icon = getIcon(notification.type)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { handleNotificationPress(notification) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .then(
                                        if (!notification.read) Modifier.background(color.copy(alpha = 0.05f)) else Modifier
                                    )
                            ) {
                                // Unread Indicator line
                                Box(
                                    modifier = Modifier
                                        .width(4.dp)
                                        .height(IntrinsicSize.Min)
                                        .background(if (!notification.read) Color(0xFF0D9488) else Color.Transparent)
                                )

                                Row(
                                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Row(modifier = Modifier.weight(1f)) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .background(color.copy(alpha = 0.15f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = notification.title,
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color(0xFF1E293B),
                                                    modifier = Modifier.weight(1f, fill = false),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                if (!notification.read) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Box(modifier = Modifier.size(8.dp).background(Color(0xFF0D9488), CircleShape))
                                                }
                                            }

                                            Text(
                                                text = notification.message,
                                                fontSize = 14.sp,
                                                color = Color(0xFF64748B),
                                                lineHeight = 20.sp,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.padding(vertical = 6.dp)
                                            )

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Outlined.Schedule, contentDescription = null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = formatTimestamp(notification.timestamp),
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF9CA3AF)
                                                )
                                            }
                                        }
                                    }

                                    IconButton(
                                        onClick = { notificationToDelete = notification.id },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (notificationToDelete != null) {
                AlertDialog(
                    onDismissRequest = { notificationToDelete = null },
                    title = { Text("Delete Notification", fontWeight = FontWeight.SemiBold) },
                    text = { Text("Are you sure you want to delete this notification?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                notifications = notifications.filter { it.id != notificationToDelete }
                                notificationToDelete = null
                            }
                        ) {
                            Text("Delete", color = Color(0xFFEF4444), fontWeight = FontWeight.SemiBold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { notificationToDelete = null }) {
                            Text("Cancel", color = Color(0xFF64748B))
                        }
                    },
                    containerColor = Color.White
                )
            }
        }
    }
}

