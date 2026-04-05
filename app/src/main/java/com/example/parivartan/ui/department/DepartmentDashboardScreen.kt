package com.example.parivartan.ui.department

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

private val Teal500 = Color(0xFF14B8A6)
private val Teal600 = Color(0xFF0D9488)
private val Slate50 = Color(0xFFF8FAFC)
private val Slate200 = Color(0xFFE2E8F0)
private val Slate500 = Color(0xFF64748B)
private val Slate700 = Color(0xFF334155)
private val Slate800 = Color(0xFF1E293B)

private val StatusPending = Color(0xFFFFC107)
private val StatusInProgress = Color(0xFF1565C0)
private val StatusResolved = Color(0xFF66BB6A)

private val PriorityHigh = Color(0xFFDC2626)
private val PriorityMedium = Color(0xFFFFC107)
private val PriorityLow = Color(0xFF66BB6A)

private data class Grievance(
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val citizenName: String,
    val citizenPhone: String,
    var status: String,
    var priority: String,
    val location: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartmentDashboardScreen(
    onNavigateGrievances: (() -> Unit)? = null
) {
    var grievances by remember {
        mutableStateOf(
            listOf(
                Grievance("G1234567", "Pothole on Main Street", "Huge pothole causing traffic issues", "06 Apr 2026", "John Doe", "+91 9876543210", "Pending", "High", "Main Street, Central"),
                Grievance("G2345678", "Broken Streetlight", "Streetlight is out since 3 days", "05 Apr 2026", "Jane Smith", "+91 8765432109", "In Progress", "Medium", "1st Avenue, North"),
                Grievance("G3456789", "Water pipe leak", "Continuous water leakage", "04 Apr 2026", "Mike Johnson", "+91 7654321098", "Resolved", "High", "Park Road, South"),
                Grievance("G4567890", "Fallen tree branch", "Blocking the pedestrian path", "03 Apr 2026", "Sarah Lee", "+91 6543210987", "Pending", "Low", "Green Street, East")
            )
        )
    }

    var selectedGrievance by remember { mutableStateOf<Grievance?>(null) }
    var activeFilter by remember { mutableStateOf("All") }

    val filteredGrievances = remember(grievances, activeFilter) {
        if (activeFilter == "All") grievances else grievances.filter { it.status == activeFilter }
    }

    val totalCount = grievances.size
    val pendingCount = grievances.count { it.status == "Pending" }
    val inProgressCount = grievances.count { it.status == "In Progress" }
    val resolvedCount = grievances.count { it.status == "Resolved" }

    Scaffold(
        containerColor = Slate50,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("PWD Department", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Welcome, Officer", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Teal600,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { /* Logout */ }) {
                        Icon(Icons.Outlined.Logout, contentDescription = "Logout", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard("Total", totalCount.toString(), Teal600, Modifier.weight(1f))
                    StatCard("Pending", pendingCount.toString(), StatusPending, Modifier.weight(1f))
                    StatCard("In Progress", inProgressCount.toString(), StatusInProgress, Modifier.weight(1f))
                    StatCard("Resolved", resolvedCount.toString(), StatusResolved, Modifier.weight(1f))
                }
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.FilterAlt, contentDescription = "Filter", tint = Slate500, modifier = Modifier.padding(end = 8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val filters = listOf("All", "Pending", "In Progress", "Resolved")
                        items(filters) { filter ->
                            FilterChip(
                                selected = activeFilter == filter,
                                onClick = { activeFilter = filter },
                                label = { Text(filter) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Teal50,
                                    selectedLabelColor = Teal600
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    selected = activeFilter == filter,
                                    enabled = true,
                                    borderColor = Teal600
                                )
                            )
                        }
                    }
                }
            }

            items(filteredGrievances) { grievance ->
                GrievanceCard(grievance) {
                    selectedGrievance = grievance
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    selectedGrievance?.let { grievance ->
        GrievanceDetailDialog(
            grievance = grievance,
            onDismiss = { selectedGrievance = null },
            onUpdate = { newStatus, newPriority ->
                grievances = grievances.map {
                    if(it.id == grievance.id) it.copy(status = newStatus, priority = newPriority) else it
                }
                selectedGrievance = null
            }
        )
    }
}

val Teal50 = Color(0xFFF0FDFA)

@Composable
private fun StatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            Text(title, fontSize = 10.sp, color = Slate500, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun GrievanceCard(grievance: Grievance, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("#${grievance.id}", style = MaterialTheme.typography.labelMedium, color = Slate500)
                Text(grievance.date, style = MaterialTheme.typography.labelMedium, color = Slate500)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(grievance.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Slate800)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Badge(
                    text = grievance.status,
                    color = when(grievance.status) {
                        "Pending" -> StatusPending
                        "In Progress" -> StatusInProgress
                        else -> StatusResolved
                    }
                )
                Badge(
                    text = "${grievance.priority} Priority",
                    color = when(grievance.priority) {
                        "High" -> PriorityHigh
                        "Medium" -> PriorityMedium
                        else -> PriorityLow
                    }
                )
            }
        }
    }
}

@Composable
private fun Badge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, color = color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GrievanceDetailDialog(
    grievance: Grievance,
    onDismiss: () -> Unit,
    onUpdate: (status: String, priority: String) -> Unit
) {
    var status by remember { mutableStateOf(grievance.status) }
    var priority by remember { mutableStateOf(grievance.priority) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Grievance Details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Slate200)

                Text(grievance.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Slate800)
                Text(grievance.description, style = MaterialTheme.typography.bodyMedium, color = Slate700, modifier = Modifier.padding(top = 4.dp, bottom = 16.dp))

                Text("Citizen Info", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = Slate500)
                Text("${grievance.citizenName} • ${grievance.citizenPhone}", style = MaterialTheme.typography.bodyMedium, color = Slate800)
                Text(grievance.location, style = MaterialTheme.typography.bodySmall, color = Slate500, modifier = Modifier.padding(bottom = 16.dp))

                Text("Update Status", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = Slate500, modifier = Modifier.padding(bottom = 4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 12.dp)) {
                    listOf("Pending", "In Progress", "Resolved").forEach { option ->
                        FilterChip(
                            selected = status == option,
                            onClick = { status = option },
                            label = { Text(option) },
                        )
                    }
                }

                Text("Update Priority", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = Slate500, modifier = Modifier.padding(bottom = 4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("High", "Medium", "Low").forEach { level ->
                        FilterChip(
                            selected = priority == level,
                            onClick = { priority = level },
                            label = { Text(level) },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onUpdate(status, priority)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Teal600,
                        contentColor = Color.White
                    )
                ) {
                    Text("Update Grievance")
                }
            }
        }
    }
}
