package com.example.parivartan.ui.staff

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class Comment(
    val id: String,
    val text: String,
    val author: String,
    val timestamp: Long
)

// A mocked Issue class just for this screen since we don't have the shared one.
data class StaffIssue(
    val id: String,
    val title: String,
    val description: String,
    val priority: String,
    val status: String,
    val locationAddress: String,
    val locationLat: Double?,
    val locationLng: Double?,
    val reporterName: String,
    val reporterContact: String,
    val upvotes: Int,
    val photos: List<String>,
    val comments: List<Comment>,
    val createdAt: Long,
    val updatedAt: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffIssueDetailScreen(
    issueId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Mock initial data
    var issue by remember {
        mutableStateOf(
            StaffIssue(
                id = issueId,
                title = "Pothole on Main St",
                description = "Large pothole causing traffic slowdowns and potential vehicle damage near the intersection.",
                priority = "high",
                status = "pending",
                locationAddress = "Main St, City Center",
                locationLat = 31.330000,
                locationLng = 75.584400,
                reporterName = "Aarav Sharma",
                reporterContact = "+91 9876543210",
                upvotes = 15,
                photos = listOf("https://via.placeholder.com/150"),
                comments = listOf(
                    Comment("c1", "Please look into this ASAP.", "Aarav Sharma", System.currentTimeMillis() - 86400000)
                ),
                createdAt = System.currentTimeMillis() - 86400000 * 2,
                updatedAt = System.currentTimeMillis() - 86400000
            )
        )
    }

    var newComment by remember { mutableStateOf("") }
    var uploading by remember { mutableStateOf(false) }

    var showStatusDialog by remember { mutableStateOf<String?>(null) }

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

    val formatDate = { dateMillis: Long ->
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        sdf.format(Date(dateMillis))
    }

    val handleNavigate = {
        val lat = issue.locationLat
        val lng = issue.locationLng
        if (lat != null && lng != null) {
            val uri = Uri.parse("google.navigation:q=$lat,$lng")
            val mapIntent = Intent(Intent.ACTION_VIEW, uri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(mapIntent)
            } else {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lng")
                )
                context.startActivity(browserIntent)
            }
        }
    }

    val handleAddComment = {
        if (newComment.isNotBlank()) {
            val comment = Comment(
                id = "c${System.currentTimeMillis()}",
                text = newComment,
                author = "Staff User",
                timestamp = System.currentTimeMillis()
            )
            issue = issue.copy(
                comments = issue.comments + comment,
                updatedAt = System.currentTimeMillis()
            )
            newComment = ""
        }
    }

    val handlePickImage: () -> Unit = {
        uploading = true
        coroutineScope.launch {
            delay(1000)
            issue = issue.copy(
                photos = issue.photos + "https://via.placeholder.com/150/0000FF",
                updatedAt = System.currentTimeMillis()
            )
            uploading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Issue Details", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        @Suppress("DEPRECATION")
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Issue Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE5E7EB))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(issue.id, fontSize = 14.sp, color = Color(0xFF6B7280), fontWeight = FontWeight.SemiBold)
                        Row(
                            modifier = Modifier
                                .background(getPriorityColor(issue.priority), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Flag, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Text(issue.priority.uppercase(), fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(
                        text = issue.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Box(
                        modifier = Modifier
                            .background(getStatusColor(issue.status), RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            issue.status.replace("-", " ").replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Issue Details
            CardSection(title = "Description", icon = Icons.Outlined.Description, iconTint = Color(0xFF0D9488)) {
                Text(issue.description, fontSize = 14.sp, color = Color(0xFF334155), lineHeight = 22.sp)
            }

            // Location
            CardSection(title = "Location", icon = Icons.Outlined.LocationOn, iconTint = Color(0xFF0D9488)) {
                Text(issue.locationAddress, fontSize = 14.sp, color = Color(0xFF334155), modifier = Modifier.padding(bottom = 12.dp))
                if (issue.locationLat != null && issue.locationLng != null) {
                    Button(
                        onClick = handleNavigate,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Outlined.Navigation, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Navigate", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Reporter Info
            CardSection(title = "Reporter Information", icon = Icons.Outlined.Person, iconTint = Color(0xFF0D9488)) {
                InfoRow("Name:", issue.reporterName)
                InfoRow("Contact:", issue.reporterContact)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Upvotes:", fontSize = 14.sp, color = Color(0xFF64748B))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = Color(0xFF0D9488), modifier = Modifier.size(14.dp))
                        Text(issue.upvotes.toString(), fontSize = 14.sp, color = Color(0xFF0D9488), fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }

            // Photos
            CardSection(title = "Photos", icon = Icons.Outlined.Image, iconTint = Color(0xFF0D9488)) {
                if (issue.photos.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(issue.photos) { photo ->
                            AsyncImage(
                                model = photo,
                                contentDescription = "Issue Photo",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                } else {
                    Text("No photos available", fontSize = 14.sp, color = Color(0xFF94A3B8), modifier = Modifier.padding(bottom = 12.dp))
                }

                OutlinedButton(
                    onClick = handlePickImage,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0D9488))
                ) {
                    if (uploading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color(0xFF0D9488), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Uploading...")
                    } else {
                        Icon(Icons.Outlined.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Upload Photo", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Update Status
            val statusOptions = listOf(
                StatusOption("pending", "Pending", Icons.Outlined.Schedule),
                StatusOption("acknowledged", "Acknowledged", Icons.Outlined.Check),
                StatusOption("in-progress", "In Progress", Icons.Outlined.RocketLaunch),
                StatusOption("resolved", "Resolved", Icons.Outlined.CheckCircle),
                StatusOption("cannot-resolve", "Cannot Resolve", Icons.Outlined.Cancel)
            )

            CardSection(title = "Update Status", icon = Icons.Outlined.Refresh, iconTint = Color(0xFF0D9488)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    statusOptions.forEach { option ->
                        val isActive = issue.status == option.value
                        val oColor = getStatusColor(option.value)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showStatusDialog = option.value }
                                .background(if (isActive) Color(0xFF0D9488) else Color.White, RoundedCornerShape(8.dp))
                                .border(2.dp, if (isActive) Color(0xFF0D9488) else oColor, RoundedCornerShape(8.dp))
                                .padding(vertical = 12.dp, horizontal = 16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = option.icon,
                                    contentDescription = null,
                                    tint = if (isActive) Color.White else oColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = option.label,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isActive) Color.White else Color(0xFF334155)
                                )
                            }
                        }
                    }
                }
            }

            // Comments
            CardSection(title = "Comments (${issue.comments.size})", icon = Icons.Outlined.ChatBubbleOutline, iconTint = Color(0xFF0D9488)) {
                if (issue.comments.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        issue.comments.forEach { comment ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(comment.author, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
                                        Text(formatDate(comment.timestamp), fontSize = 11.sp, color = Color(0xFF64748B))
                                    }
                                    Text(comment.text, fontSize = 14.sp, color = Color(0xFF334155), lineHeight = 20.sp)
                                }
                            }
                        }
                    }
                } else {
                    Text("No comments yet", fontSize = 14.sp, color = Color(0xFF94A3B8), modifier = Modifier.padding(bottom = 12.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newComment,
                        onValueChange = { newComment = it },
                        placeholder = { Text("Add a comment...", fontSize = 14.sp) },
                        modifier = Modifier.weight(1f).heightIn(min = 44.dp, max = 100.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            focusedBorderColor = Color(0xFF0D9488)
                        ),
                        singleLine = false,
                        shape = RoundedCornerShape(8.dp)
                    )
                    IconButton(
                        onClick = handleAddComment,
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFF0D9488), RoundedCornerShape(8.dp))
                    ) {
                        @Suppress("DEPRECATION")
                        Icon(Icons.Filled.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }

            // Timestamps
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Created: ${formatDate(issue.createdAt)}", fontSize = 12.sp, color = Color(0xFF6B7280))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.AccessTime, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Updated: ${formatDate(issue.updatedAt)}", fontSize = 12.sp, color = Color(0xFF6B7280))
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (showStatusDialog != null) {
        val newStatus = showStatusDialog!!
        AlertDialog(
            onDismissRequest = { showStatusDialog = null },
            title = { Text("Update Status") },
            text = { Text("Change status to \"${newStatus.replace("-", " ")}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    issue = issue.copy(status = newStatus, updatedAt = System.currentTimeMillis())
                    showStatusDialog = null
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStatusDialog = null }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
private fun CardSection(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF111827))
            }
            content()
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp, color = Color(0xFF6B7280))
        Text(value, fontSize = 14.sp, color = Color(0xFF111827), fontWeight = FontWeight.Medium)
    }
}

private data class StatusOption(val value: String, val label: String, val icon: ImageVector)

