package com.example.parivartan.ui.issue

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.drawBehind
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueDetailScreen(
    issueId: String,
    onNavigateBack: () -> Unit
) {
    var activeTab by remember { mutableStateOf("details") } // 'details', 'comments', 'updates'
    var isUpvoted by remember { mutableStateOf(false) }
    var upvotes by remember { mutableStateOf(12) }
    var commentText by remember { mutableStateOf("") }
    var showRatingModal by remember { mutableStateOf(false) }
    var rating by remember { mutableStateOf(0) }
    var ratingComment by remember { mutableStateOf("") }

    val statusColor = Color(0xFFF59E0B) // Amber for pending/under_review
    val statusBg = Color(0xFFFEF3C7)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("INFRASTRUCTURE", fontSize = 12.sp, color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
                        Text("Pothole on Main Road", fontSize = 18.sp, color = Color(0xFF334155), fontWeight = FontWeight.SemiBold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(paddingValues)
        ) {
            // Status and Upvote Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(statusBg, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Under Review", color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("Reported on Oct 12, 2023", color = Color(0xFF64748B), fontSize = 12.sp)
                }

                Row(
                    modifier = Modifier
                        .background(if (isUpvoted) Color(0xFF0D9488) else Color(0xFFF1F5F9), RoundedCornerShape(16.dp))
                        .clickable {
                            isUpvoted = !isUpvoted
                            if (isUpvoted) upvotes++ else upvotes--
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = "Upvote",
                        tint = if (isUpvoted) Color.White else Color(0xFF334155),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("$upvotes", color = if (isUpvoted) Color.White else Color(0xFF334155), fontSize = 14.sp)
                }
            }

            // Tabs
            Row(modifier = Modifier.fillMaxWidth().background(Color.White)) {
                TabItem("Details", activeTab == "details", Modifier.weight(1f)) { activeTab = "details" }
                TabItem("Comments (2)", activeTab == "comments", Modifier.weight(1f)) { activeTab = "comments" }
                TabItem("Updates (1)", activeTab == "updates", Modifier.weight(1f)) { activeTab = "updates" }
            }

            // Content
            Box(modifier = Modifier.weight(1f)) {
                when (activeTab) {
                    "details" -> DetailsTab(onRateClick = { showRatingModal = true })
                    "comments" -> CommentsTab(commentText, { commentText = it }, { commentText = "" })
                    "updates" -> UpdatesTab()
                }
            }
        }

        // Rating Modal
        if (showRatingModal) {
            AlertDialog(
                onDismissRequest = { showRatingModal = false },
                title = { Text("Rate This Resolution", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                            (1..5).forEach { star ->
                                IconButton(onClick = { rating = star }) {
                                    Icon(
                                        if (star <= rating) Icons.Default.Star else Icons.Default.Star,
                                        contentDescription = "Star",
                                        tint = if (star <= rating) Color(0xFFFBBF24) else Color(0xFFCBD5E1),
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                        }
                        OutlinedTextField(
                            value = ratingComment,
                            onValueChange = { ratingComment = it },
                            placeholder = { Text("Add a comment (optional)") },
                            modifier = Modifier.fillMaxWidth().height(100.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showRatingModal = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                        enabled = rating > 0
                    ) {
                        Text("Submit", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRatingModal = false }) {
                        Text("Cancel", color = Color(0xFF334155))
                    }
                }
            )
        }
    }
}

@Composable
fun TabItem(text: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
            .drawBehind {
                if (isSelected) {
                    drawLine(
                        Color(0xFF0D9488),
                        start = androidx.compose.ui.geometry.Offset(0f, size.height),
                        end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                        strokeWidth = 4.dp.toPx()
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (isSelected) Color(0xFF0D9488) else Color(0xFF64748B), fontWeight = FontWeight.Medium)
    }
}

@Composable
fun DetailsTab(onRateClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Description
        Text("Description", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
        Spacer(Modifier.height(8.dp))
        Text(
            "There is a huge pothole on the main road which is causing trouble for daily commuters and risks accidents.",
            fontSize = 14.sp,
            color = Color(0xFF334155),
            lineHeight = 20.sp
        )
        Spacer(Modifier.height(24.dp))

        // Images Placeholder
        Text("Images", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
        Spacer(Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth().height(150.dp).background(Color(0xFFE2E8F0), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
            Text("Image Gallery", color = Color(0xFF64748B))
        }
        Spacer(Modifier.height(24.dp))

        // Location Placeholder
        Text("Location", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)).padding(12.dp)
        ) {
            Column {
                Text("Main Road, Model Town, Jalandhar", fontSize = 14.sp, color = Color(0xFF334155))
                Spacer(Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth().height(150.dp).background(Color(0xFFE2E8F0), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                    Text("Map View Placeholder", color = Color(0xFF64748B))
                }
            }
        }
        Spacer(Modifier.height(24.dp))

        // Reported By
        Text("Reported by", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(40.dp).background(Color(0xFFCBD5E1), CircleShape))
            Spacer(Modifier.width(12.dp))
            Column {
                Text("John Doe", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155))
                Text("on Oct 12, 2023", fontSize = 12.sp, color = Color(0xFF64748B))
            }
        }
        Spacer(Modifier.height(24.dp))

        // Resolution Feedback
        Text("Resolution Feedback", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = onRateClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("Rate Resolution", color = Color.White)
        }
    }
}

@Composable
fun CommentsTab(commentText: String, onCommentChange: (String) -> Unit, onSubmit: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)).padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = commentText,
                onValueChange = onCommentChange,
                placeholder = { Text("Add a comment...") },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.Transparent, focusedBorderColor = Color.Transparent)
            )
            IconButton(onClick = onSubmit, enabled = commentText.isNotBlank()) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = if (commentText.isNotBlank()) Color(0xFF0D9488) else Color(0xFFCBD5E1))
            }
        }
        Spacer(Modifier.height(16.dp))

        // Comments List Dummy
        LazyColumn {
            items(2) { index ->
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Box(modifier = Modifier.size(36.dp).background(Color(0xFFCBD5E1), CircleShape))
                    Spacer(Modifier.width(12.dp))
                    Column(
                        modifier = Modifier.weight(1f).background(Color.White, RoundedCornerShape(8.dp)).padding(12.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Jane Smith", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Text("Oct 13", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("I agree, this has been an issue for weeks.", fontSize = 14.sp, color = Color(0xFF334155))
                    }
                }
            }
        }
    }
}

@Composable
fun UpdatesTab() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Updates List Dummy
        LazyColumn {
            items(1) {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(24.dp)) {
                        Box(modifier = Modifier.size(12.dp).background(Color(0xFF0D9488), CircleShape))
                        Box(modifier = Modifier.width(2.dp).height(50.dp).background(Color(0xFFE2E8F0)))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(
                        modifier = Modifier.weight(1f).background(Color.White, RoundedCornerShape(8.dp)).padding(12.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Box(modifier = Modifier.background(Color(0xFFFEF3C7), RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                Text("Under Review", color = Color(0xFFF59E0B), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                            Text("Oct 14", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Issue has been assigned to PWD for inspection.", fontSize = 14.sp, color = Color(0xFF334155))
                    }
                }
            }
        }
    }
}