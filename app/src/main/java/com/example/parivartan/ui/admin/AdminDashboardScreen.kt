package com.example.parivartan.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

    Scaffold(
        containerColor = Slate50,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Admin Dashboard", color = Color.White, fontWeight = FontWeight.Bold)
                            Text(
                                "Welcome, admin! Manage all departments and grievances from here.",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(end = 16.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF10B981)),
                    actions = {
                        IconButton(onClick = { /* TODO: logout hook */ }) {
                            Icon(Icons.Outlined.Logout, contentDescription = "Logout", tint = Color.White)
                        }
                    }
                )
                // Segmented navbar with 4 items
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .border(1.dp, Color(0xFF16A34A), RoundedCornerShape(999.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val tabs = listOf("Overview", "Departments", "All Grievances", "Analytics")
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = if (isSelected) Color(0xFF16A34A) else Color.Transparent,
                                    shape = RoundedCornerShape(999.dp)
                                )
                                .clickable { selectedTab = index }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                color = if (isSelected) Color.White else Color(0xFF0F172A),
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (selectedTab == 1) {
                FloatingActionButton(onClick = { /* TODO: open Add Department dialog */ }, containerColor = Teal600) {
                    Icon(Icons.Default.Add, contentDescription = "Add Department", tint = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> OverviewTab()
                1 -> DepartmentManagementTab()
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
            StatCard("Total Grievances", "14", Teal600, Modifier.weight(1f))
            StatCard("Resolved", "0", Color(0xFF22C55E), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard("In Progress", "7", Color(0xFF2563EB), Modifier.weight(1f))
            StatCard("Pending", "5", Color(0xFFF97316), Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Department Performance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PerformanceRow("Civil Surgeon’s Office", 100f)
                PerformanceRow("Punjab Police", 100f)
                PerformanceRow("Water Supply & Sanitation", 100f)
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, accentColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.bodySmall, color = Slate500)
            Spacer(Modifier.height(6.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .fillMaxWidth()
                    .background(Color(0xFFE2E8F0), RoundedCornerShape(999.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(4.dp)
                        .background(accentColor, RoundedCornerShape(999.dp))
                )
            }
        }
    }
}

@Composable
fun PerformanceRow(department: String, percentage: Float) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(department, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${percentage.toInt()}% Resolved", style = MaterialTheme.typography.bodySmall, color = Slate500)
        }
        LinearProgressIndicator(
            progress = percentage / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .padding(top = 4.dp),
            color = Teal600,
            trackColor = Color(0xFFE2E8F0)
        )
    }
}

@Composable
fun DepartmentManagementTab() {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Department Management",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF14532D)
            )
            OutlinedButton(
                onClick = { /* TODO: open Add Department dialog */ },
                shape = RoundedCornerShape(999.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp, brush = SolidColor(Color(0xFF16A34A)))
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF16A34A))
                Spacer(Modifier.width(6.dp))
                Text("Add Department", color = Color(0xFF16A34A))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val departments = remember { sampleDepartmentCards() }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 260.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(departments) { dept ->
                DepartmentCard(dept)
            }
        }
    }
}

data class DepartmentUiModel(
    val id: String,
    val name: String,
    val head: String,
    val officer: String,
    val contact: String,
    val email: String,
    val total: Int,
    val resolved: Int,
    val pending: Int
)

@Composable
private fun DepartmentCard(dept: DepartmentUiModel) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dept.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827),
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                TextButton(
                    onClick = { /* TODO edit department */ },
                    colors = ButtonDefaults.textButtonColors(containerColor = Color(0xFF16A34A), contentColor = Color.White),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Edit", fontSize = 12.sp)
                }
                Spacer(Modifier.width(6.dp))
                TextButton(
                    onClick = { /* TODO delete department */ },
                    colors = ButtonDefaults.textButtonColors(containerColor = Color(0xFFDC2626), contentColor = Color.White),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Outlined.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Delete", fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Info rows
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Department Head:", fontSize = 12.sp, color = Slate500)
                Text(dept.head, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF111827))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Assigned Officer:", fontSize = 12.sp, color = Slate500)
                Text(dept.officer, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF111827))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Contact:", fontSize = 12.sp, color = Slate500)
                Text(dept.contact, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF111827))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Email:", fontSize = 12.sp, color = Slate500)
                Text(dept.email, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF111827))
            }

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF9FAFB), RoundedCornerShape(12.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricColumn("Total\nGrievances", dept.total, Color(0xFF111827))
                MetricColumn("Resolved", dept.resolved, Color(0xFF16A34A))
                MetricColumn("Pending", dept.pending, Color(0xFFF97316))
            }
        }
    }
}

@Composable
private fun MetricColumn(label: String, value: Int, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 6.dp)) {
        Text(label, fontSize = 11.sp, color = Slate500, textAlign = TextAlign.Center, lineHeight = 14.sp)
        Spacer(Modifier.height(4.dp))
        Text(value.toString(), fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}

private fun sampleDepartmentCards(): List<DepartmentUiModel> = listOf(
    DepartmentUiModel(
        id = "civil-surgeon",
        name = "Civil Surgeon's Office",
        head = "Civil Surgeon",
        officer = "civil-surgeon",
        contact = "+91-XXXXXXXXXX",
        email = "civil-surgeon@parivartan.gov.in",
        total = 0,
        resolved = 0,
        pending = 0
    ),
    DepartmentUiModel(
        id = "forest",
        name = "Forest Department",
        head = "Forest Department",
        officer = "forest",
        contact = "+91-XXXXXXXXXX",
        email = "forest@parivartan.gov.in",
        total = 1,
        resolved = 0,
        pending = 1
    ),
    DepartmentUiModel(
        id = "water-sanitation",
        name = "Water Supply & Sanitation",
        head = "Water & Sanitation",
        officer = "water-sanitation",
        contact = "+91-XXXXXXXXXX",
        email = "watersanitation@parivartan.gov.in",
        total = 2,
        resolved = 0,
        pending = 2
    )
    // ...add other departments similarly or plug into real data source later
)

// ---------- All Grievances TAB ----------

@Composable
fun GrievancesTab() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "All Grievances",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF14532D)
        )
        Spacer(Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Header row similar to web table
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TableHeaderCell("ID", Modifier.weight(1.2f))
                    TableHeaderCell("Title", Modifier.weight(2.2f))
                    TableHeaderCell("Department", Modifier.weight(2.2f))
                    TableHeaderCell("Status", Modifier.weight(1.2f))
                    TableHeaderCell("Priority", Modifier.weight(1.2f))
                    TableHeaderCell("Citizen", Modifier.weight(1.4f))
                    TableHeaderCell("Date", Modifier.weight(1.4f))
                    TableHeaderCell("Actions", Modifier.weight(1.4f), textAlign = TextAlign.End)
                }
                Divider(color = Color(0xFFE2E8F0))

                val grievances = remember { sampleAdminGrievances() }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 4.dp)
                ) {
                    items(grievances.size) { index ->
                        val g = grievances[index]
                        GrievanceRow(g)
                        if (index != grievances.lastIndex) {
                            Divider(color = Color(0xFFF1F5F9))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TableHeaderCell(text: String, modifier: Modifier = Modifier, textAlign: TextAlign = TextAlign.Start) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.SemiBold,
        color = Slate500,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = textAlign
    )
}

// Simple row model for admin grievances list

data class AdminGrievanceRow(
    val id: String,
    val title: String,
    val department: String,
    val status: String,
    val priority: String,
    val citizen: String,
    val date: String
)

@Composable
private fun GrievanceRow(row: AdminGrievanceRow) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TableBodyText(row.id, Modifier.weight(1.2f))
        TableBodyText(row.title, Modifier.weight(2.2f))
        TableBodyText(row.department, Modifier.weight(2.2f))
        StatusChip(row.status, Modifier.weight(1.2f))
        PriorityChip(row.priority, Modifier.weight(1.2f))
        TableBodyText(row.citizen, Modifier.weight(1.4f))
        TableBodyText(row.date, Modifier.weight(1.4f))
        Row(
            modifier = Modifier.weight(1.4f),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "View",
                color = Teal600,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .border(1.dp, Teal600, RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun TableBodyText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFF111827),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun StatusChip(status: String, modifier: Modifier = Modifier) {
    val (bg, fg) = when (status.lowercase()) {
        "pending" -> Color(0xFFFACC15) to Color(0xFF713F12)
        "in-progress" -> Color(0xFF9CA3AF) to Color.White
        "resolved" -> Color(0xFF22C55E) to Color.White
        else -> Color(0xFFE5E7EB) to Color(0xFF111827)
    }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .background(bg, RoundedCornerShape(999.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = status.uppercase(),
                color = fg,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
        }
    }
}

@Composable
private fun PriorityChip(priority: String, modifier: Modifier = Modifier) {
    val (bg, fg) = when (priority.lowercase()) {
        "high" -> Color(0xFFEF4444) to Color.White
        "medium" -> Color(0xFFF97316) to Color.White
        "low" -> Color(0xFF22C55E) to Color.White
        else -> Color(0xFFE5E7EB) to Color(0xFF111827)
    }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .background(bg, RoundedCornerShape(999.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = priority.uppercase(),
                color = fg,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
        }
    }
}

private fun sampleAdminGrievances(): List<AdminGrievanceRow> = listOf(
    AdminGrievanceRow("2NCM1GVQ", "Test", "Public Works Department (PWD)", "PENDING", "MEDIUM", "Rajath", "14/11/2025"),
    AdminGrievanceRow("CH4EQNSW", "Aws test", "Public Works Department (PWD)", "PENDING", "MEDIUM", "Rajath", "14/11/2025"),
    AdminGrievanceRow("FEBF0S24", "Aws test", "Public Works Department (PWD)", "PENDING", "MEDIUM", "Rajath", "14/11/2025"),
    AdminGrievanceRow("2WD0ILL3", "Aws issue", "Public Works Department (PWD)", "PENDING", "MEDIUM", "Rajath", "14/11/2025"),
    AdminGrievanceRow("NVPFEBZ5", "Water problem", "Public Works Department (PWD)", "IN-PROGRESS", "MEDIUM", "Rajath", "14/11/2025"),
    AdminGrievanceRow("4CDDW0CJ", "Demo", "Public Works Department (PWD)", "IN-PROGRESS", "MEDIUM", "Rajath", "01/11/2025"),
    AdminGrievanceRow("HLKREKIM", "Final Demo", "Public Works Department (PWD)", "IN-PROGRESS", "MEDIUM", "Aarush", "01/11/2025"),
    AdminGrievanceRow("T7E2DZP6", "Water issue", "Water Supply & Sanitation", "IN-PROGRESS", "MEDIUM", "Aarush", "01/11/2025")
)

// ---------- Analytics TAB ----------

// Data models for analytics UI

data class DepartmentAnalytics(val name: String, val count: Int)
data class StatusAnalytics(val status: String, val count: Int, val percentage: Int)
data class PriorityAnalytics(val priority: String, val count: Int, val percentage: Int)

@Composable
fun AnalyticsTab() {
    // Mock analytics data – later can be wired to Firestore/REST
    val byDepartment = remember {
        listOf(
            DepartmentAnalytics("Civil Surgeon’s Office", 1),
            DepartmentAnalytics("Forest Department", 1),
            DepartmentAnalytics("Traffic Police", 1),
            DepartmentAnalytics("Water Supply & Sanitation", 1),
            DepartmentAnalytics("School Education", 1),
            DepartmentAnalytics("Punjab Roadways / PRTC", 1),
            DepartmentAnalytics("Punjab Police", 2),
            DepartmentAnalytics("Municipal Corporation", 1),
            DepartmentAnalytics("Food & Civil Supplies", 1),
            DepartmentAnalytics("Revenue Department", 1),
            DepartmentAnalytics("Social Security & Women & Child", 1),
            DepartmentAnalytics("Public Works Department (PWD)", 9)
        )
    }
    val byStatus = remember {
        listOf(
            StatusAnalytics("Pending", 5, 36),
            StatusAnalytics("In Progress", 7, 50),
            StatusAnalytics("Resolved", 0, 0),
            StatusAnalytics("Rejected", 2, 14)
        )
    }
    val byPriority = remember {
        listOf(
            PriorityAnalytics("High Priority", 0, 0),
            PriorityAnalytics("Medium Priority", 14, 100),
            PriorityAnalytics("Low Priority", 0, 0)
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Analytics & Reports",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF14532D)
        )

        // Three analytics cards stacked vertically for better phone responsiveness
        AnalyticsDepartmentCard(byDepartment)
        AnalyticsStatusCard(byStatus)
        AnalyticsPriorityCard(byPriority)
    }
}

@Composable
private fun AnalyticsDepartmentCard(items: List<DepartmentAnalytics>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Grievances by Department",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF111827)
            )
            Spacer(Modifier.height(12.dp))

            val max = (items.maxOfOrNull { it.count } ?: 1).coerceAtLeast(1)

            items.forEach { item ->
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF111827),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = item.count.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(Color(0xFFE2E8F0), RoundedCornerShape(999.dp))
                    ) {
                        val ratio = item.count.toFloat() / max.toFloat()
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(ratio.coerceIn(0f, 1f))
                                .height(8.dp)
                                .background(Teal600, RoundedCornerShape(999.dp))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsStatusCard(items: List<StatusAnalytics>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Grievances by Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF111827)
            )
            Spacer(Modifier.height(12.dp))

            items.forEach { item ->
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.status,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF111827),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${item.count} (${item.percentage}%)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(Color(0xFFE2E8F0), RoundedCornerShape(999.dp))
                    ) {
                        val ratio = (item.percentage / 100f).coerceIn(0f, 1f)
                        val barColor = when (item.status.lowercase()) {
                            "pending" -> Color(0xFFFACC15)
                            "in progress" -> Color(0xFF2563EB)
                            "resolved" -> Color(0xFF22C55E)
                            else -> Teal600
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(ratio)
                                .height(8.dp)
                                .background(barColor, RoundedCornerShape(999.dp))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsPriorityCard(items: List<PriorityAnalytics>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Grievances by Priority",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF111827)
            )
            Spacer(Modifier.height(12.dp))

            items.forEach { item ->
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.priority,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF111827),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${item.count} (${item.percentage}%)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(Color(0xFFE2E8F0), RoundedCornerShape(999.dp))
                    ) {
                        val ratio = (item.percentage / 100f).coerceIn(0f, 1f)
                        val barColor = when {
                            item.priority.contains("High", ignoreCase = true) -> Color(0xFFEF4444)
                            item.priority.contains("Medium", ignoreCase = true) -> Color(0xFFF97316)
                            else -> Teal600
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(ratio)
                                .height(8.dp)
                                .background(barColor, RoundedCornerShape(999.dp))
                        )
                    }
                }
            }
        }
    }
}
