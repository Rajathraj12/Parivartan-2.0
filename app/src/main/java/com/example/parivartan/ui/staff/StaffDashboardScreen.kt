package com.example.parivartan.ui.staff

import androidx.compose.foundation.background
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parivartan.data.IssueRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue

data class Issue(
    val id: String,
    val title: String,
    val priority: String,
    val status: String,
    val location: String
)

@Composable
fun StaffDashboardScreen(
    onNavigateIssues: () -> Unit,
    onNavigateMap: () -> Unit,
    onNavigateNotifications: () -> Unit,
    onNavigateSettings: () -> Unit,
    onNavigateToIssueDetail: (String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val issueRepository = remember { IssueRepository() }
    val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
    val userName = user?.displayName?.takeIf { it.isNotBlank() } ?: "Staff Member"
    val userStaffId = "EMP-${user?.uid?.take(4)?.uppercase() ?: "0000"}"
    val departmentName = ""

    var stats by remember { mutableStateOf(mapOf(
        "total" to 0,
        "pending" to 0,
        "inProgress" to 0,
        "resolved" to 0
    )) }
    var assignedIssues by remember { mutableStateOf<List<Issue>>(emptyList()) }

    var isVisible by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        isVisible = true
        // Find tasks assigned to this actual user by name (or use a fallback mapping if needed, trying actual name first)
        // If the user's name is "staff1", we check for either "staff1", or "Staff Member 1" if assignments still use the mock combo
        val staffNameToSearch = if (userName.contains("staff1", ignoreCase = true)) "Staff Member 1" else userName
        val res = issueRepository.getIssuesAssignedToStaff(staffNameToSearch)
        if (res.isSuccess) {
            val dbIssues = res.getOrDefault(emptyList())
            stats = mapOf(
                "total" to dbIssues.size,
                "pending" to dbIssues.count { it.status == "pending" || it.status == "assigned" },
                "inProgress" to dbIssues.count { it.status == "in-progress" || it.status == "in_progress" },
                "resolved" to dbIssues.count { it.status == "resolved" }
            )
            assignedIssues = dbIssues.map {
                Issue(
                    id = it.id,
                    title = it.title,
                    priority = it.status, // Fallback priority if not on model
                    status = it.status,
                    location = it.locationAddress
                )
            }
        }
    }

    val scrollState = rememberScrollState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(280.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(Color(0xFF14B8A6), Color(0xFF0D9488))))
                        .padding(24.dp)
                ) {
                    Column {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                .padding(12.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = userName,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Staff ID: $userStaffId",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Outlined.List, contentDescription = null) },
                    label = { Text("View All Issues") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        onNavigateIssues()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Map, contentDescription = null) },
                    label = { Text("Map View") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        onNavigateMap()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        onNavigateSettings()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = "Logout") },
                    label = { Text("Logout", color = Color(0xFFEF4444)) },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        onLogout()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold { innerPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFF8FAFC))
                    .verticalScroll(scrollState)
            ) {
                // Profile Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            translationY = scrollState.value * 0.5f // Parallax effect
                        }
                        .background(Brush.linearGradient(listOf(Color(0xFF14B8A6), Color(0xFF0D9488))))
                        .padding(top = 60.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { coroutineScope.launch { drawerState.open() } },
                                modifier = Modifier.padding(end = 12.dp)
                            ) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                            }

                            // Soft glow wrapper
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(Color.White.copy(alpha = 0.3f), shape = CircleShape)
                                    .padding(4.dp), // creates an outer glow layer
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
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
                            }

                            Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                            Text("Welcome back,", color = Color(0xCCFFFFFF), fontSize = 14.sp)
                            Text(
                                text = userName,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            if (departmentName.isNotEmpty()) {
                                Text(departmentName, color = Color(0xCCFFFFFF), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                            }

                            Box(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("ID: $userStaffId", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        }

                        Box(contentAlignment = Alignment.TopEnd) {
                            // Bouncing bell animation
                            val bounceAnim by androidx.compose.animation.core.animateFloatAsState(
                                targetValue = if (isVisible) 0f else -20f,
                                animationSpec = androidx.compose.animation.core.spring(
                                    dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                                    stiffness = androidx.compose.animation.core.Spring.StiffnessLow
                                )
                            )
                            IconButton(onClick = onNavigateNotifications) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .graphicsLayer { translationY = bounceAnim }
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .padding(top = 6.dp, end = 6.dp)
                                    .size(10.dp)
                                    .graphicsLayer { translationY = bounceAnim }
                                    .background(Color(0xFFF59E0B), CircleShape)
                                    .border(1.dp, Color(0xFF0D9488), CircleShape)
                            )
                        }
                    }
                }

                // Stats Grid
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(animationSpec = tween(500)) + slideInVertically(animationSpec = tween(500), initialOffsetY = { it / 2 })
                ) {
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
                }

                // Assigned Tasks
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(animationSpec = tween(700))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Assigned Tasks", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), modifier = Modifier.padding(bottom = 16.dp))

                        if (assignedIssues.isEmpty()) {
                            Text("No tasks assigned yet.", color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                assignedIssues.forEach { issue ->
                                    IssueCard(
                                        issue = issue,
                                        onClick = { onNavigateToIssueDetail(issue.id) }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(80.dp)) // Extra padding for FAB
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val targetValue = value.toIntOrNull() ?: 0
    val animatedValue by animateIntAsState(
        targetValue = targetValue,
        animationSpec = tween(durationMillis = 1000)
    )
    val displayValue = if (value.toIntOrNull() != null) animatedValue.toString() else value

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp)
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
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(color.copy(alpha = 0.1f), CircleShape)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(displayValue, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                    Text(label, fontSize = 12.sp, color = Color(0xFF6B7280), modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueCard(
    issue: Issue,
    onClick: () -> Unit
) {
    val statusColor = when (issue.status.lowercase()) {
        "pending", "assigned" -> Color(0xFFEAB308)
        "in-progress", "in_progress" -> Color(0xFF3B82F6)
        "resolved", "completed" -> Color(0xFF10B981)
        "cannot-resolve" -> Color(0xFFEF4444)
        else -> Color(0xFF6B7280)
    }

    val priorityColor = when (issue.priority.lowercase()) {
        "high" -> Color(0xFFEF4444)
        "medium" -> Color(0xFFEAB308)
        "low" -> Color(0xFF10B981)
        else -> Color(0xFF6B7280)
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.98f else 1f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(color = statusColor),
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPressed) 8.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
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
fun QuickActionButton(
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
