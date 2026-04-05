package com.example.parivartan.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Teal600 = Color(0xFF0D9488)
private val Slate50 = Color(0xFFF8FAFC)
private val Slate500 = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateGlobalAnalytics: (() -> Unit)? = null,
    onNavigateDepartmentManagement: (() -> Unit)? = null
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Departments", "Grievances", "Analytics")

    Scaffold(
        containerColor = Slate50,
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Admin Dashboard", color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Teal600),
                    actions = {
                        IconButton(onClick = { /* Logout */ }) {
                            Icon(Icons.Outlined.Logout, contentDescription = "Logout", tint = Color.White)
                        }
                    }
                )
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Teal600,
                    contentColor = Color.White
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (selectedTab == 1) { // Departments tab
                FloatingActionButton(onClick = { /* Add Department */ }, containerColor = Teal600) {
                    Icon(Icons.Default.Add, contentDescription = "Add Department", tint = Color.White)
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            when (selectedTab) {
                0 -> OverviewTab()
                1 -> DepartmentsTab()
                2 -> GrievancesTab()
                3 -> AnalyticsTab()
            }
        }
    }
}

@Composable
fun OverviewTab() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard("Total Reports", "1,204", Teal600, Modifier.weight(1f))
            StatCard("Resolved", "843", Color(0xFF66BB6A), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard("In Progress", "230", Color(0xFF1565C0), Modifier.weight(1f))
            StatCard("Pending", "131", Color(0xFFFFC107), Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Department Performance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PerformanceRow("PWD", 85f)
                PerformanceRow("Water & Sanitation", 72f)
                PerformanceRow("Traffic Police", 91f)
            }
        }
    }
}

@Composable
fun PerformanceRow(department: String, percentage: Float) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(department, style = MaterialTheme.typography.bodyMedium)
            Text("${percentage.toInt()}% Resolved", style = MaterialTheme.typography.bodySmall, color = Slate500)
        }
        LinearProgressIndicator(
            progress = percentage / 100f,
            modifier = Modifier.fillMaxWidth().height(8.dp).padding(top = 4.dp),
            color = Teal600,
            trackColor = Color(0xFFE2E8F0)
        )
    }
}

@Composable
fun DepartmentsTab() {
    val departments = listOf(
        "PWD - Head: Ramesh Kumar (12 Pending)",
        "Water Supply - Head: Sunita Rao (8 Pending)",
        "Traffic - Head: Arvind Singh (3 Pending)"
    )
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(departments) { dept ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(dept, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun GrievancesTab() {
    Text("Global Grievance View (Filter by Department)")
    // Simplified stub to meet length constraints. Looks similar to Dept Dashboard.
}

@Composable
fun AnalyticsTab() {
    Text("Charts and Analytics Dashboard")
}

@Composable
private fun StatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = color)
            Text(title, fontSize = 12.sp, color = Slate500)
        }
    }
}