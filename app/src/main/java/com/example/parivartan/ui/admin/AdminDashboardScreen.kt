package com.example.parivartan.ui.admin

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private val Teal600 = Color(0xFF0D9488)
private val Slate500 = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    modifier: Modifier = Modifier
) {
    var stats by remember { mutableStateOf(mapOf(
        "total" to 0,
        "pending" to 0,
        "inProgress" to 0,
        "resolved" to 0
    )) }

    var isVisible by remember { mutableStateOf(false) }
    var currentTab by remember { mutableStateOf("Overview") }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val userName = "System Administrator"

    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(Unit) {
        isVisible = true
        try {
            val result = db.collection("issues").get().await()
            val total = result.size()
            val pending = result.count { it.getString("status")?.uppercase() == "PENDING" }
            val inProgress = result.count { it.getString("status")?.uppercase() == "IN-PROGRESS" || it.getString("status")?.uppercase() == "IN PROGRESS" }
            val resolved = result.count { it.getString("status")?.uppercase() == "RESOLVED" }

            stats = mapOf(
                "total" to total,
                "pending" to pending,
                "inProgress" to inProgress,
                "resolved" to resolved
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val scrollState = rememberScrollState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        modifier = modifier,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(280.dp), drawerContainerColor = Color.White) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(Color(0xFF0D9488), Color(0xFF0F766E))))
                        .padding(24.dp)
                ) {
                    Column {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
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
                            text = "Admin Access",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                    label = { Text("Overview") },
                    selected = currentTab == "Overview",
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        currentTab = "Overview"
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Outlined.List, contentDescription = null) },
                    label = { Text("Manage Departments") },
                    selected = currentTab == "Departments",
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        currentTab = "Departments"
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Report, contentDescription = null) },
                    label = { Text("Grievances") },
                    selected = currentTab == "Grievances",
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        currentTab = "Grievances"
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Analytics, contentDescription = null) },
                    label = { Text("Global Analytics") },
                    selected = currentTab == "Analytics",
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        currentTab = "Analytics"
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Outlined.ExitToApp, contentDescription = "Logout", tint = Color(0xFFEF4444)) },
                    label = { Text("Logout", color = Color(0xFFEF4444)) },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        FirebaseAuth.getInstance().signOut()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            when(currentTab) {
                                "Overview" -> "Admin Dashboard"
                                "Departments" -> "Manage Departments"
                                "Grievances" -> "All Grievances"
                                "Analytics" -> "Global Analytics"
                                else -> "Admin Dashboard"
                            },
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color(0xFF1E293B))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            },
            containerColor = Color(0xFFF8FAFC)
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when(currentTab) {
                    "Overview" -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                        ) {
                            // Header Profile Card
                            AnimatedVisibility(
                                visible = isVisible,
                                enter = fadeIn(animationSpec = tween(500)) + slideInVertically(animationSpec = tween(500), initialOffsetY = { -it / 2 })
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    shape = RoundedCornerShape(20.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Brush.linearGradient(listOf(Color(0xFF0D9488), Color(0xFF0F766E))))
                                            .padding(24.dp)
                                    ) {
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        "Welcome Back,",
                                                        color = Color.White.copy(alpha = 0.8f),
                                                        fontSize = 14.sp
                                                    )
                                                    Text(
                                                        userName,
                                                        color = Color.White,
                                                        fontSize = 24.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(top = 4.dp)
                                                    )
                                                }
                                                Icon(
                                                    imageVector = Icons.Default.AdminPanelSettings,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier
                                                        .size(48.dp)
                                                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                                        .padding(10.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(24.dp))
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                                    .padding(16.dp),
                                                horizontalArrangement = Arrangement.SpaceAround
                                            ) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(stats["total"].toString(), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                                    Text("All Issues", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                                                }
                                                VerticalDivider(modifier = Modifier.height(30.dp).width(1.dp).background(Color.White.copy(alpha = 0.3f)))
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(stats["pending"].toString(), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                                    Text("Pending", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Grid Statistics
                            AnimatedVisibility(
                                visible = isVisible,
                                enter = fadeIn(animationSpec = tween(500)) + slideInVertically(animationSpec = tween(500), initialOffsetY = { it / 2 })
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        AdminStatCard(
                                            modifier = Modifier.weight(1f),
                                            icon = Icons.Default.Inventory,
                                            label = "Total Issues",
                                            value = stats["total"].toString(),
                                            color = Color(0xFF0D9488)
                                        )
                                        AdminStatCard(
                                            modifier = Modifier.weight(1f),
                                            icon = Icons.Default.AccessTime,
                                            label = "Pending",
                                            value = stats["pending"].toString(),
                                            color = Color(0xFFF59E0B)
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        AdminStatCard(
                                            modifier = Modifier.weight(1f),
                                            icon = Icons.Default.RocketLaunch,
                                            label = "In Progress",
                                            value = stats["inProgress"].toString(),
                                            color = Color(0xFF3B82F6)
                                        )
                                        AdminStatCard(
                                            modifier = Modifier.weight(1f),
                                            icon = Icons.Default.CheckCircle,
                                            label = "Resolved",
                                            value = stats["resolved"].toString(),
                                            color = Color(0xFF10B981)
                                        )
                                    }
                                }
                            }

                            // Global Overview
                            AnimatedVisibility(
                                visible = isVisible,
                                enter = fadeIn(animationSpec = tween(700))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("System Overview", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), modifier = Modifier.padding(bottom = 16.dp))

                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                            AdminPerformanceRow("Civil Surgeon’s Office", 100f)
                                            AdminPerformanceRow("Punjab Police", 85f)
                                            AdminPerformanceRow("Water Supply & Sanitation", 72f)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(80.dp))
                                }
                            }
                        }
                    }
                    "Departments" -> {
                        Box(modifier = Modifier.padding(16.dp)) {
                            DepartmentManagementTab()
                        }
                    }
                    "Grievances" -> {
                        Box(modifier = Modifier.padding(16.dp)) {
                            GrievancesTab()
                        }
                    }
                    "Analytics" -> {
                        Box(modifier = Modifier.padding(16.dp).verticalScroll(scrollState)) {
                            AnalyticsTab()
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStatCard(
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

@Composable
fun AdminPerformanceRow(department: String, percentage: Float) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(department, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow. Ellipsis)
            Text("${percentage.toInt()}% Resolved", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
        }
        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .padding(top = 4.dp),
            color = Color(0xFF0D9488),
            trackColor = Color(0xFFE2E8F0)
        )
    }
}


@Composable
fun DepartmentManagementTab() {
    Column(modifier = Modifier.fillMaxSize()) {

        var departments by remember { mutableStateOf(sampleDepartmentCards().sortedByDescending { it.total }) }
        var departmentToEdit by remember { mutableStateOf<DepartmentUiModel?>(null) }
        val db = FirebaseFirestore.getInstance()

        LaunchedEffect(Unit) {
            try {
                val result = db.collection("issues").get().await()
                val updatedDepts = departments.map { dept ->
                    val deptGrievances = result.filter { it.getString("department") == dept.name }
                    dept.copy(
                        total = deptGrievances.size,
                        resolved = deptGrievances.count { it.getString("status")?.uppercase() == "RESOLVED" },
                        pending = deptGrievances.count { it.getString("status")?.uppercase() == "PENDING" }
                    )
                }.sortedByDescending { it.total }
                departments = updatedDepts
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 260.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(departments) { dept ->
                DepartmentCard(
                    dept = dept,
                    onEdit = { departmentToEdit = it },
                    onDelete = {
                        departments = departments.filter { it.id != dept.id }
                    }
                )
            }
        }

        departmentToEdit?.let { dept ->
            EditDepartmentDialog(
                department = dept,
                onDismiss = { departmentToEdit = null },
                onSave = { updatedDept ->
                    departments = departments.map { if (it.id == updatedDept.id) updatedDept else it }.sortedByDescending { it.total }
                    departmentToEdit = null
                }
            )
        }
    }
}

@Composable
fun EditDepartmentDialog(
    department: DepartmentUiModel,
    onDismiss: () -> Unit,
    onSave: (DepartmentUiModel) -> Unit
) {
    var name by remember { mutableStateOf(department.name) }
    var head by remember { mutableStateOf(department.head) }
    var officer by remember { mutableStateOf(department.officer) }
    var contact by remember { mutableStateOf(department.contact) }
    var email by remember { mutableStateOf(department.email) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Department") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = head, onValueChange = { head = it }, label = { Text("Head") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = officer, onValueChange = { officer = it }, label = { Text("Assigned Officer") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = contact, onValueChange = { contact = it }, label = { Text("Contact") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(department.copy(name = name, head = head, officer = officer, contact = contact, email = email))
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
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
private fun DepartmentCard(
    dept: DepartmentUiModel,
    onEdit: (DepartmentUiModel) -> Unit,
    onDelete: (DepartmentUiModel) -> Unit
) {
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
                    onClick = { onEdit(dept) },
                    colors = ButtonDefaults.textButtonColors(containerColor = Color(0xFF16A34A), contentColor = Color.White),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Edit", fontSize = 12.sp)
                }
                Spacer(Modifier.width(6.dp))
                TextButton(
                    onClick = { onDelete(dept) },
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
        officer = "Dr. Sharma",
        contact = "+91-9876543210",
        email = "civil-surgeon@parivartan.gov.in",
        total = 0,
        resolved = 0,
        pending = 0
    ),
    DepartmentUiModel(
        id = "forest",
        name = "Forest Department",
        head = "Chief Conservator",
        officer = "Mr. Singh",
        contact = "+91-9876543211",
        email = "forest@parivartan.gov.in",
        total = 0,
        resolved = 0,
        pending = 0
    ),
    DepartmentUiModel(
        id = "traffic-police",
        name = "Traffic Police",
        head = "DCP Traffic",
        officer = "Mr. Yadav",
        contact = "+91-9876543212",
        email = "traffic@parivartan.gov.in",
        total = 0,
        resolved = 0,
        pending = 0
    ),
    DepartmentUiModel(
        id = "water-sanitation",
        name = "Water Supply & Sanitation",
        head = "Chief Engineer",
        officer = "Mr. Gupta",
        contact = "+91-9876543213",
        email = "watersanitation@parivartan.gov.in",
        total = 0,
        resolved = 0,
        pending = 0
    ),
    DepartmentUiModel(
        id = "school-education",
        name = "School Education",
        head = "District Education Officer",
        officer = "Mrs. Kaur",
        contact = "+91-9876543214",
        email = "education@parivartan.gov.in",
        total = 0,
        resolved = 0,
        pending = 0
    ),
    DepartmentUiModel(
        id = "roadways",
        name = "Punjab Roadways / PRTC",
        head = "General Manager",
        officer = "Mr. Singh",
        contact = "+91-9876543215",
        email = "roadways@parivartan.gov.in",
        total = 0,
        resolved = 0,
        pending = 0
    ),
    DepartmentUiModel(
        id = "punjab-police",
        name = "Punjab Police",
        head = "SSP",
        officer = "Mr. Kumar",
        contact = "+91-9876543216",
        email = "police@parivartan.gov.in",
        total = 0,
        resolved = 0,
        pending = 0
    ),
    DepartmentUiModel(
        id = "municipal",
        name = "Municipal Corporation",
        head = "Commissioner",
        officer = "Mr. Verma",
        contact = "+91-9876543217",
        email = "mc@parivartan.gov.in",
        total = 0,
        resolved = 0,
        pending = 0
    ),
    DepartmentUiModel(
        id = "food-civil",
        name = "Food & Civil Supplies",
        head = "District Controller",
        officer = "Mr. Singh",
        contact = "+91-9876543218",
        email = "food@parivartan.gov.in",
        total = 0,
        resolved = 0,
        pending = 0
    ),
    DepartmentUiModel(
        id = "revenue",
        name = "Revenue Department",
        head = "Deputy Commissioner",
        officer = "Mrs. Sharma",
        contact = "+91-9876543219",
        email = "revenue@parivartan.gov.in",
        total = 0,
        resolved = 0,
        pending = 0
    ),
    DepartmentUiModel(
        id = "social-security",
        name = "Social Security & Women & Child",
        head = "District Officer",
        officer = "Mrs. Verma",
        contact = "+91-9876543220",
        email = "social@parivartan.gov.in",
        total = 0,
        resolved = 0,
        pending = 0
    ),
    DepartmentUiModel(
        id = "pwd",
        name = "Public Works Department (PWD)",
        head = "Superintending Engineer",
        officer = "Mr. Singh",
        contact = "+91-9876543221",
        email = "pwd@parivartan.gov.in",
        total = 0,
        resolved = 0,
        pending = 0
    )
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

        var grievances by remember { mutableStateOf<List<AdminGrievanceRow>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }
        var selectedGrievance by remember { mutableStateOf<AdminGrievanceRow?>(null) }
        val db = FirebaseFirestore.getInstance()

        LaunchedEffect(Unit) {
            try {
                val result = db.collection("issues").get().await()
                val list = result.map { doc ->
                    val dateLong = doc.getLong("createdAt") ?: 0L
                    val dateFormatted = if (dateLong > 0) java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date(dateLong)) else ""
                    AdminGrievanceRow(
                        id = doc.id,
                        title = doc.getString("title") ?: "No Title",
                        description = doc.getString("description") ?: "",
                        location = doc.getString("location") ?: "",
                        department = doc.getString("department") ?: "Unknown",
                        status = doc.getString("status") ?: "PENDING",
                        priority = doc.getString("priority") ?: "MEDIUM",
                        citizen = doc.getString("reporterName") ?: "Citizen",
                        date = dateFormatted
                    )
                }
                grievances = list
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Teal600)
            }
        } else if (grievances.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No grievances found.", color = Slate500)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(grievances.size) { index ->
                    val g = grievances[index]
                    AdminGrievanceCard(g, onViewDetails = { selectedGrievance = g })
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        selectedGrievance?.let { g ->
            AdminGrievanceDetailDialog(g, onDismiss = { selectedGrievance = null })
        }
    }
}

// Simple row model for admin grievances list

data class AdminGrievanceRow(
    val id: String,
    val title: String,
    val description: String,
    val location: String,
    val department: String,
    val status: String,
    val priority: String,
    val citizen: String,
    val date: String
)

@Composable
private fun AdminGrievanceDetailDialog(
    grievance: AdminGrievanceRow,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Issue Details", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(top = 8.dp)) {
                Text("ID: ${grievance.id}", fontSize = 12.sp, color = Slate500)
                Spacer(Modifier.height(8.dp))
                Text("Title: ${grievance.title}", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color(0xFF111827))
                Spacer(Modifier.height(8.dp))
                Text("Date: ${grievance.date}", fontSize = 14.sp, color = Slate500)
                Spacer(Modifier.height(8.dp))
                Text("Department: ${grievance.department}", fontSize = 14.sp, color = Color(0xFF111827))
                Spacer(Modifier.height(8.dp))
                Text("Status: ${grievance.status.uppercase()}", fontSize = 14.sp, color = Color(0xFF111827))
                Spacer(Modifier.height(8.dp))
                Text("Priority: ${grievance.priority.uppercase()}", fontSize = 14.sp, color = Color(0xFF111827))
                Spacer(Modifier.height(8.dp))
                Text("Reporter: ${grievance.citizen}", fontSize = 14.sp, color = Color(0xFF111827))
                Spacer(Modifier.height(16.dp))
                Text("Description:", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Slate500)
                Text(grievance.description.ifEmpty { "N/A" }, fontSize = 14.sp, color = Color(0xFF111827))
                Spacer(Modifier.height(16.dp))
                Text("Location:", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Slate500)
                Text(grievance.location.ifEmpty { "N/A" }, fontSize = 14.sp, color = Color(0xFF111827))
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Teal600)) {
                Text("Close", color = Color.White)
            }
        }
    )
}

@Composable
private fun AdminGrievanceCard(
    row: AdminGrievanceRow,
    onViewDetails: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID: ${row.id}",
                    fontSize = 12.sp,
                    color = Slate500,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = row.date,
                    fontSize = 12.sp,
                    color = Slate500
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = row.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF111827)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = row.department,
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusChip(row.status)
                    PriorityChip(row.priority)
                }
                Text(
                    text = "By: ${row.citizen}",
                    fontSize = 12.sp,
                    color = Color(0xFF111827),
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = onViewDetails,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(999.dp),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(width = 1.dp, brush = SolidColor(Teal600))
            ) {
                Text("View Details", color = Teal600)
            }
        }
    }
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


// ---------- Analytics TAB ----------

// Data models for analytics UI

data class DepartmentAnalytics(val name: String, val count: Int)
data class StatusAnalytics(val status: String, val count: Int, val percentage: Int)
data class PriorityAnalytics(val priority: String, val count: Int, val percentage: Int)

@Composable
fun AnalyticsTab() {
    var byDepartment by remember { mutableStateOf<List<DepartmentAnalytics>>(emptyList()) }
    var byStatus by remember { mutableStateOf<List<StatusAnalytics>>(emptyList()) }
    var byPriority by remember { mutableStateOf<List<PriorityAnalytics>>(emptyList()) }
    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(Unit) {
        try {
            val result = db.collection("issues").get().await()
            val total = result.size()

            val deptMap = mutableMapOf<String, Int>()
            val statusMap = mutableMapOf<String, Int>()
            val priorityMap = mutableMapOf<String, Int>()

            result.forEach { doc ->
                val dept = doc.getString("department") ?: "Unknown"
                val status = doc.getString("status") ?: "PENDING"
                val priority = doc.getString("priority") ?: "MEDIUM"

                deptMap[dept] = deptMap.getOrDefault(dept, 0) + 1
                statusMap[status] = statusMap.getOrDefault(status, 0) + 1
                priorityMap[priority] = priorityMap.getOrDefault(priority, 0) + 1
            }

            byDepartment = deptMap.map { DepartmentAnalytics(it.key, it.value) }.sortedByDescending { it.count }

            byStatus = listOf("Pending", "In Progress", "Resolved", "Rejected").map { s ->
                val count = statusMap.entries.firstOrNull { it.key.equals(s, ignoreCase = true) }?.value ?: 0
                val percentage = if (total > 0) (count * 100) / total else 0
                StatusAnalytics(s, count, percentage)
            }

            byPriority = listOf("High", "Medium", "Low").map { p ->
                val count = priorityMap.entries.firstOrNull { it.key.equals(p, ignoreCase = true) }?.value ?: 0
                val percentage = if (total > 0) (count * 100) / total else 0
                PriorityAnalytics("$p Priority", count, percentage)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
